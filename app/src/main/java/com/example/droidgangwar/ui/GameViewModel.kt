package com.example.droidgangwar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.droidgangwar.data.GameRepository
import com.example.droidgangwar.data.RandomEventData
import com.example.droidgangwar.model.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    private val _combatResult = MutableLiveData<CombatResult>()
    val combatResult: LiveData<CombatResult> = _combatResult

    private val _gameMessage = MutableLiveData<String>()
    val gameMessage: LiveData<String> = _gameMessage

    private val _currentScreen = MutableLiveData<String>("main_menu")
    val currentScreen: LiveData<String> = _currentScreen

    // Store current combat data for fragments to access
    var currentCombatData: CombatData? = null

    init {
        loadGameState()
    }

    fun loadGameState() {
        viewModelScope.launch {
            val savedState = repository.loadGameState()
            if (savedState == null || savedState.playerName.isNullOrEmpty()) {
                // No valid saved state, start fresh with start screen
                _gameState.value = GameState()
                _currentScreen.value = "start_game"
            } else {
                _gameState.value = savedState
                _currentScreen.value = savedState.currentLocation ?: "city"
            }
        }
    }

    fun saveGameState() {
        _gameState.value?.let { state ->
            viewModelScope.launch {
                repository.saveGameState(state)
            }
        }
    }

    fun startNewGame(playerName: String, gangName: String) {
        val newGameState = GameState(
            playerName = playerName,
            gangName = gangName
        )
        _gameState.value = newGameState
        _currentScreen.value = "city"
        saveGameState()
    }

    fun restartGame() {
        // Clear game state but maybe keep preferences if any
        // For now, total wipe for new game feeling
        _gameState.value = GameState()
        _currentScreen.value = "start_game"
        saveGameState()
        _gameMessage.value = "Game restarted! Enter your name and gang name."
    }

    fun navigateToScreen(screen: String) {
        _currentScreen.value = screen
        _gameState.value?.currentLocation = screen
        saveGameState()
    }

    fun startCombat(enemyType: String, enemyCount: Int, enemyHealth: Int) {
        // Navigate to mud fight with combat parameters
        // This will be handled by the fragment navigation system
        _currentScreen.value = "mud_fight"
        saveGameState()
    }

    fun startMudFight(enemyHealth: Int, enemyCount: Int, enemyType: String, combatId: String) {
        // Store combat parameters temporarily
        _combatResult.value = CombatResult().apply {
            this.enemiesKilled = 0
            this.fightLog.add("Combat starting: $enemyType ($enemyCount enemies)")
        }
        
        // Store combat data in a way the fragment can access it
        currentCombatData = CombatData(
            enemyHealth = enemyHealth.toDouble(),
            enemyCount = enemyCount,
            enemyType = enemyType,
            combatId = combatId,
            initialMessage = "You engage in combat with $enemyType!"
        )
        
        // Navigate to mud fight screen with proper navigation
        _currentScreen.value = "mud_fight"
        
        // Save state and trigger navigation
        _gameState.value?.let { gameState ->
            saveGameState()
        }
        
        // Trigger a message to show navigation is happening
        _gameMessage.value = "Starting combat with $enemyType!"
    }

    fun buyWeapon(weaponType: String, quantity: Int = 1): Boolean {
        val gameState = _gameState.value ?: return false

        val price = when (weaponType) {
            "pistol" -> 1200
            "bullets" -> 100
            "exploding_bullets" -> 500
            "uzi" -> 100000
            "grenade" -> 1000
            "barbed_wire_bat" -> 2500
            "missile_launcher" -> 1000000
            "missile" -> 100000
            "vest_light" -> 30000
            "vest_medium" -> 55000
            "vest_heavy" -> 75000
            else -> return false
        }

        val totalCost = price * quantity

        if (!gameState.spendMoney(totalCost)) {
            _gameMessage.value = "Not enough money!"
            return false
        }

        when (weaponType) {
            "pistol" -> gameState.weapons.pistols += quantity
            "bullets" -> gameState.weapons.bullets += quantity * 50
            "exploding_bullets" -> gameState.weapons.explodingBullets += quantity * 20
            "uzi" -> gameState.weapons.uzis += quantity
            "grenade" -> gameState.weapons.grenades += quantity
            "barbed_wire_bat" -> gameState.weapons.barbedWireBat += quantity
            "missile_launcher" -> gameState.weapons.missileLauncher += quantity
            "missile" -> gameState.weapons.missiles += quantity
            "vest_light" -> gameState.weapons.vest = 5 // Set to specific defense value
            "vest_medium" -> gameState.weapons.vest = 10 // Set to specific defense value
            "vest_heavy" -> gameState.weapons.vest = 15 // Set to specific defense value
        }

        _gameMessage.value = "Purchased $quantity $weaponType(s) for $${totalCost.formatWithCommas()}!"
        saveGameState()
        return true
    }
    
    fun toggleExplodingBullets(enable: Boolean) {
        val gameState = _gameState.value ?: return
        gameState.weapons.useExplodingBullets = enable
        saveGameState()
    }

    fun tradeDrug(drugType: String, action: String, quantity: Int): Boolean {
        val gameState = _gameState.value ?: return false

        val price = gameState.drugPrices[drugType] ?: return false

        return when (action) {
            "buy" -> {
                val cost = price * quantity
                if (gameState.spendMoney(cost)) {
                    when (drugType) {
                        "weed" -> gameState.drugs.weed += quantity
                        "crack" -> gameState.drugs.crack += quantity
                        "coke" -> gameState.drugs.coke += quantity
                        "ice" -> gameState.drugs.ice += quantity
                        "percs" -> gameState.drugs.percs += quantity
                        "pixie_dust" -> gameState.drugs.pixieDust += quantity
                    }
                    _gameMessage.value = "Bought $quantity kilo(s) of $drugType for $${cost.formatWithCommas()}!"
                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough money!"
                    false
                }
            }
            "sell" -> {
                val currentQty = when (drugType) {
                    "weed" -> gameState.drugs.weed
                    "crack" -> gameState.drugs.crack
                    "coke" -> gameState.drugs.coke
                    "ice" -> gameState.drugs.ice
                    "percs" -> gameState.drugs.percs
                    "pixie_dust" -> gameState.drugs.pixieDust
                    else -> 0
                }

                if (currentQty >= quantity) {
                    val revenue = price * quantity
                    gameState.money += revenue

                    when (drugType) {
                        "weed" -> gameState.drugs.weed -= quantity
                        "crack" -> gameState.drugs.crack -= quantity
                        "coke" -> gameState.drugs.coke -= quantity
                        "ice" -> gameState.drugs.ice -= quantity
                        "percs" -> gameState.drugs.percs -= quantity
                        "pixie_dust" -> gameState.drugs.pixieDust -= quantity
                    }

                    _gameMessage.value = "Sold $quantity kilo(s) of $drugType for $${revenue.formatWithCommas()}!"

                    // Chance to recruit new member from big drug sales
                    if (revenue >= 5000 && Random.nextFloat() < 0.25f) {
                        gameState.members++
                        _gameMessage.value += "\nWord spread! A new recruit joined your gang!"
                    }

                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough $drugType to sell!"
                    false
                }
            }
            else -> false
        }
    }

    fun performCombat(weapon: String, enemyType: String, enemyCount: Int): Boolean {
        val gameState = _gameState.value ?: return false

        val enemyHealth: Double = when (enemyType) {
            "Police Officers" -> enemyCount * 10.0
            "Squidie Hit Squad" -> enemyCount * 25.0
            else -> enemyCount * 15.0
        }

        val result = CombatSystem.calculateCombat(gameState, weapon, enemyType, enemyCount, enemyHealth)
        _combatResult.value = result

        if (result.victory) {
            _gameMessage.value = "Victory! You defeated the $enemyType!"
        } else if (result.defeat) {
            _gameMessage.value = "Defeat! Game Over!"
            _currentScreen.value = "game_over"
        }

        saveGameState()
        return result.victory
    }

    fun useDrug(drug: String) {
        val gameState = _gameState.value ?: return
        val result = CombatSystem.useDrug(gameState, drug)
        _gameMessage.value = result.message
        if (result.success) {
            if (result.healthChange != 0) {
                if (result.healthChange > 0) {
                    gameState.heal(result.healthChange)
                } else {
                    gameState.takeDamage(-result.healthChange.toInt())
                }
            }
            // Update drug count if successful is handled in CombatSystem for percs and crack but double check
            // CombatSystem methods don't update game state directly for item counts usually unless passed by ref or mutable
            // Let's update counts here to be safe/sure or verify CombatSystem does it.
            // Checking CombatSystem... it does update gameState.drugs properties directly. Good.
        }
        saveGameState()
    }

    fun fleeCombat(): Boolean {
        val (success, message) = CombatSystem.fleeCombat()
        _gameMessage.value = message
        if (success) {
            saveGameState()
        }
        return success
    }

    fun visitProstitutes(action: String): Boolean {
        val gameState = _gameState.value ?: return false

        return when (action) {
            "quick_service" -> {
                if (gameState.spendMoney(200)) {
                    val healthGain = (5..15).random()
                    gameState.heal(healthGain)
                    _gameMessage.value = "You enjoyed a quick service and gained $healthGain health!"
                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough money!"
                    false
                }
            }
            "vip_experience" -> {
                if (gameState.spendMoney(500)) {
                    val healthGain = (15..30).random()
                    gameState.heal(healthGain)
                    _gameMessage.value = "You had a VIP experience and gained $healthGain health!"
                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough money!"
                    false
                }
            }
            "recruit_hooker" -> {
                if (gameState.spendMoney(1000)) {
                    gameState.members++
                    val healthGain = (10..20).random()
                    gameState.heal(healthGain)
                    _gameMessage.value = "You recruited a hooker to your gang and gained $healthGain health!"
                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough money!"
                    false
                }
            }
            else -> false
        }
    }

    fun picknsaveAction(action: String): Boolean {
        val gameState = _gameState.value ?: return false

        return when (action) {
            "buy_food" -> {
                if (gameState.spendMoney(500)) {
                    _gameMessage.value = "You bought food supplies for your gang! Morale is high."
                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough money!"
                    false
                }
            }
            "buy_medical" -> {
                if (gameState.spendMoney(1000)) {
                    gameState.heal(50)
                    _gameMessage.value = "You bought medical supplies! Health restored."
                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough money!"
                    false
                }
            }
            "buy_id" -> {
                if (gameState.spendMoney(5000)) {
                    gameState.flags.hasId = true
                    _gameMessage.value = "You bought a fake ID! Protected from police checks."
                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough money!"
                    false
                }
            }
            "buy_info" -> {
                if (gameState.spendMoney(2000)) {
                    gameState.flags.hasInfo = true
                    _gameMessage.value = "You bought information! Insider knowledge about police."
                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough money!"
                    false
                }
            }
            "recruit" -> {
                if (gameState.spendMoney(10000)) {
                    gameState.members++
                    _gameMessage.value = "You recruited a new gang member! Your gang grows stronger."
                    saveGameState()
                    true
                } else {
                    _gameMessage.value = "Not enough money!"
                    false
                }
            }
            else -> false
        }
    }

    fun updateGameState(newState: GameState) {
        _gameState.value = newState
        saveGameState()
    }

    fun incrementSteps() {
        val gameState = _gameState.value ?: return
        gameState.steps++
        
        // Check if day ends
        if (gameState.steps >= gameState.maxSteps) {
            gameState.advanceDay()
            _gameMessage.value = "A new day begins! Day ${gameState.day}"
        }
        
        saveGameState()
    }

    fun wander(): String {
        val gameState = _gameState.value ?: return "Error: No game state"

        // Increment steps
        gameState.steps++

        // Check if day ends
        if (gameState.steps >= gameState.maxSteps) {
            gameState.advanceDay()
            return "A new day begins! Day ${gameState.day}"
        }

        // Random wander events - Use RandomEventData for improved events
        val event = RandomEventData.generateRandomEvent(gameState)
        
        // Check requirements
        if (!RandomEventData.hasMeetRequirements(event, gameState)) {
            // Fallback if requirements not met
             return "You wander the streets but find nothing of interest."
        }
        
        // Apply effects
        RandomEventData.applyEventEffects(event, gameState)
        
        // Check for combat events
        when (event.type) {
            EventType.GANG_FIGHT -> {
                startMudFight(100, 3, "Rival Gang Members", event.id)
            }
            EventType.POLICE_CHASE -> {
                startMudFight(150, 4, "Police Officers", event.id)
            }
            EventType.SQUIDIE_HIT_SQUAD -> {
                 startMudFight(200, 5, "Squidie Hit Squad", event.id)
            }
             EventType.NPC_ENCOUNTER -> {
                 // Sometimes NPCs can be monsters or weirdos
                 if (Random.nextFloat() < 0.1f) {
                      startMudFight(300, 1, "Sewer Monster", event.id)
                      return "You encounter a Sewer Monster! It attacks!"
                 }
             }
             else -> {
                 // Peaceful event
             }
        }

        saveGameState()
        return event.description
    }
}

// Extension function for formatting numbers with commas
fun Int.formatWithCommas(): String {
    return String.format("%,d", this)
}

// Data class for passing combat information between components
data class CombatData(
    val enemyHealth: Double,
    val enemyCount: Int,
    val enemyType: String,
    val combatId: String,
    val initialMessage: String
)
