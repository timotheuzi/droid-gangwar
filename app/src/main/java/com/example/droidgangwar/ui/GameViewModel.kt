package com.example.droidgangwar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.droidgangwar.data.GameRepository
import com.example.droidgangwar.model.*
import kotlinx.coroutines.launch

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

    init {
        loadGameState()
    }

    fun loadGameState() {
        viewModelScope.launch {
            val savedState = repository.loadGameState()
            if (savedState != null) {
                _gameState.value = savedState
            } else {
                _gameState.value = GameState()
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

    fun buyWeapon(weaponType: String, quantity: Int = 1): Boolean {
        val gameState = _gameState.value ?: return false

        val price = when (weaponType) {
            "pistol" -> 1200
            "bullets" -> 100
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
            "uzi" -> gameState.weapons.uzis += quantity
            "grenade" -> gameState.weapons.grenades += quantity
            "barbed_wire_bat" -> gameState.weapons.barbedWireBat += quantity
            "missile_launcher" -> gameState.weapons.missileLauncher += quantity
            "missile" -> gameState.weapons.missiles += quantity
            "vest_light" -> gameState.weapons.vest += 5
            "vest_medium" -> gameState.weapons.vest += 10
            "vest_heavy" -> gameState.weapons.vest += 15
        }

        _gameMessage.value = "Purchased $quantity $weaponType(s) for $${totalCost.formatWithCommas()}!"
        saveGameState()
        return true
    }

    fun tradeDrugs(action: String, drugType: String, quantity: Int): Boolean {
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
                    if (revenue >= 5000 && Math.random() < 0.25) {
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

        val enemyHealth = when (enemyType) {
            "Police Officers" -> enemyCount * 10
            "Squidie Hit Squad" -> enemyCount * 25
            else -> enemyCount * 15
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
                    gameState.takeDamage(-result.healthChange)
                }
            }
            // Update drug count if successful
            when (drug) {
                "crack" -> gameState.drugs.crack--
                "percs" -> gameState.drugs.percs--
            }
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

    fun wander(): String {
        val gameState = _gameState.value ?: return "Error: No game state"

        // Increment steps
        gameState.steps++

        // Check if day ends
        if (gameState.steps >= gameState.maxSteps) {
            gameState.advanceDay()
            return "A new day begins! Day ${gameState.day}"
        }

        // Random wander events
        val events = listOf(
            "You wander the streets and find $50!",
            "You encounter a street performer who gives you tips.",
            "You overhear some gang members talking about turf wars.",
            "You find a quiet spot to rest and regain some health.",
            "You notice some suspicious activity but keep moving.",
            "You bump into an old contact who shares gossip.",
            "You wander into a rough neighborhood and avoid trouble.",
            "You find some discarded drugs worth $200 on the street.",
            "You help a local shopkeeper and get rewarded with information.",
            "You wander around the city without incident.",
            "You see a police patrol and quickly hide in an alley.",
            "You find a hidden stash of weapons.",
            "You encounter a beggar who tells you about secret locations."
        )

        val event = events.random()

        when {
            "find $50" in event -> gameState.money += 50
            "find $200" in event -> gameState.money += 200
            "regain some health" in event -> gameState.heal(10)
            "hidden stash of weapons" in event -> gameState.weapons.bullets += 5
        }

        saveGameState()
        return event
    }

    private fun Int.formatWithCommas(): String {
        return String.format("%,d", this)
    }
}
