package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels


import com.example.droidgangwar.databinding.FragmentMudFightBinding
import com.example.droidgangwar.model.CombatSystem
import com.example.droidgangwar.ui.GameViewModel

class MudFightFragment : Fragment() {

    private var _binding: FragmentMudFightBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    private var enemyHealth: Double = 0.0
    private var enemyCount: Int = 0
    private var enemyType: String = ""
    private var combatId: String = ""
    private val fightLog = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMudFightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get combat parameters from arguments
        arguments?.let { args ->
            enemyHealth = args.getDouble("enemy_health", 30.0)
            enemyCount = args.getInt("enemy_count", 1)
            enemyType = args.getString("enemy_type", "Enemy")
            combatId = args.getString("combat_id", "combat_${System.currentTimeMillis()}")

            // Initialize fight log
            val initialLog = args.getStringArrayList("fight_log") ?: arrayListOf("Combat begins!")
            fightLog.addAll(initialLog)
        }

        setupUI()
        setupWeaponSpinner()
        setupActionButtons()
        updateUI()
    }

    private fun setupUI() {
        binding.apply {
            // Set enemy info
            enemyNameText.text = enemyType
            enemyCountText.text = "Count: $enemyCount"
            enemyHealthText.text = "Health: $enemyHealth"

            // Update combat log
            updateCombatLog()
        }
    }

    private fun setupWeaponSpinner() {
        val gameState = gameViewModel.gameState.value ?: return

        val availableWeapons = mutableListOf<String>()

        // Add available weapons based on inventory
        if (gameState.weapons.pistols > 0 && gameState.weapons.bullets > 0) {
            availableWeapons.add("pistol")
        }
        if (gameState.weapons.ghostGuns > 0 && gameState.weapons.bullets > 0) {
            availableWeapons.add("ghost_gun")
        }
        if (gameState.weapons.uzis > 0 && gameState.weapons.bullets >= 3) {
            availableWeapons.add("uzi")
        }
        if (gameState.weapons.grenades > 0) {
            availableWeapons.add("grenade")
        }
        if (gameState.weapons.missileLauncher > 0 && gameState.weapons.missiles > 0) {
            availableWeapons.add("missile_launcher")
        }
        if (gameState.weapons.barbedWireBat > 0) {
            availableWeapons.add("barbed_wire_bat")
        }
        // Knife is always available
        availableWeapons.add("knife")

        val weaponNames = availableWeapons.map { weapon ->
            when (weapon) {
                "pistol" -> "Pistol"
                "ghost_gun" -> "Ghost Gun"
                "uzi" -> "Uzi"
                "grenade" -> "Grenade"
                "missile_launcher" -> "Missile Launcher"
                "barbed_wire_bat" -> "Barbed Wire Bat"
                "knife" -> "Knife"
                else -> weapon
            }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weaponNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.weaponSpinner.adapter = adapter
    }

    private fun setupActionButtons() {
        binding.apply {
            attackButton.setOnClickListener { performAttack() }
            defendButton.setOnClickListener { performDefend() }
            fleeButton.setOnClickListener { performFlee() }
            useDrugButton.setOnClickListener { showDrugSelection() }
            backButton.setOnClickListener { navigateBackToCity() }
        }
    }

    private fun performAttack() {
        val gameState = gameViewModel.gameState.value ?: return
        val selectedWeapon = getSelectedWeapon()

        // Perform combat
        val result = CombatSystem.calculateCombat(gameState, selectedWeapon, enemyType, enemyCount, enemyHealth)

        // Update fight log
        fightLog.addAll(result.fightLog)

        // Update enemy health and count
        enemyHealth -= result.damageDealt
        enemyCount -= result.enemiesKilled

        // Check for victory/defeat
        if (result.victory) {
            handleVictory()
        } else if (result.defeat) {
            handleDefeat()
        } else {
            // Continue combat
            updateUI()
            gameViewModel.saveGameState()
        }
    }

    private fun performDefend() {
        val gameState = gameViewModel.gameState.value ?: return

        // Reduced enemy damage
        val enemyDamage = (5..15).random() * enemyCount
        gameState.takeDamage(enemyDamage)

        fightLog.add("You defend carefully. The $enemyType deal $enemyDamage damage!")

        // Enemy attacks are reduced but still happen
        updateUI()
        gameViewModel.saveGameState()
    }

    private fun performFlee() {
        val (success, message) = CombatSystem.fleeCombat()

        fightLog.add(message)

        if (success) {
            // Successful flee
            updateUI()
            // Navigate back after a short delay
            binding.root.postDelayed({
                navigateBackToCity()
            }, 2000)
        } else {
            // Failed flee - enemy attacks
            val gameState = gameViewModel.gameState.value ?: return
            val enemyDamage = (10..20).random() * enemyCount
            gameState.takeDamage(enemyDamage)

            fightLog.add("The $enemyType attack during your escape attempt, dealing $enemyDamage damage!")

            updateUI()
            gameViewModel.saveGameState()
        }
    }

    private fun showDrugSelection() {
        val gameState = gameViewModel.gameState.value ?: return

        val availableDrugs = mutableListOf<String>()
        if (gameState.drugs.crack > 0) availableDrugs.add("crack")
        if (gameState.drugs.percs > 0) availableDrugs.add("percs")

        if (availableDrugs.isEmpty()) {
            fightLog.add("You have no drugs to use!")
            updateCombatLog()
            return
        }

        // Show drug selection dialog
        val drugNames = availableDrugs.map {
            when (it) {
                "crack" -> "Crack (damage)"
                "percs" -> "Percs (heal)"
                else -> it
            }
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Drug")
            .setItems(drugNames.toTypedArray()) { _, which ->
                val selectedDrug = availableDrugs[which]
                useDrug(selectedDrug)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun useDrug(drug: String) {
        val gameState = gameViewModel.gameState.value ?: return
        val result = CombatSystem.useDrug(gameState, drug)
        fightLog.add(result.message)
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
        updateUI()
        gameViewModel.saveGameState()
    }

    private fun getSelectedWeapon(): String {
        val selectedPosition = binding.weaponSpinner.selectedItemPosition
        val weaponNames = listOf("pistol", "ghost_gun", "uzi", "grenade", "missile_launcher", "barbed_wire_bat", "knife")
        return weaponNames.getOrElse(selectedPosition) { "knife" }
    }

    private fun handleVictory() {
        fightLog.add("🎉 COMBAT VICTORY! 🎉")
        updateUI()

        // Show victory dialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Victory!")
            .setMessage("You have defeated all enemies!")
            .setPositiveButton("Continue") { _, _ ->
                navigateBackToCity()
            }
            .setCancelable(false)
            .show()
    }

    private fun handleDefeat() {
        val gameState = gameViewModel.gameState.value ?: return

        if (gameState.lives <= 0) {
            fightLog.add("💀 FINAL DEATH - GAME OVER! 💀")
            updateUI()

            // Game over
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Game Over")
                .setMessage("You have been defeated! Game Over!")
                .setPositiveButton("Return to Main Menu") { _, _ ->
                    // Navigate to game over or main menu
                    gameViewModel.navigateToScreen("main_menu")
                }
                .setCancelable(false)
                .show()
        } else {
            fightLog.add("💥 DEFEAT! You lost a life but can continue!")
            updateUI()

            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Defeated!")
                .setMessage("You were defeated but have ${gameState.lives} lives remaining!")
                .setPositiveButton("Continue") { _, _ ->
                    navigateBackToCity()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun updateUI() {
        val gameState = gameViewModel.gameState.value ?: return

        binding.apply {
            // Update player status
            playerHealthText.text = "Health: ${gameState.health}/${gameState.maxHealth}"
            playerMoneyText.text = "Money: $${gameState.money}"
            playerLivesText.text = "Lives: ${gameState.lives}"

            // Update enemy status
            enemyCountText.text = "Count: $enemyCount"
            enemyHealthText.text = "Health: $enemyHealth"

            // Update weapon ammo counts
            ammoText.text = "Bullets: ${gameState.weapons.bullets}, Grenades: ${gameState.weapons.grenades}, Missiles: ${gameState.weapons.missiles}"

            updateCombatLog()
        }
    }

    private fun updateCombatLog() {
        val logText = fightLog.takeLast(10).joinToString("\n") // Show last 10 messages
        binding.combatLogText.text = logText

        // Auto-scroll to bottom
        binding.combatLogScroll.post {
            binding.combatLogScroll.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun navigateBackToCity() {
        gameViewModel.navigateToScreen("city")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            enemyHealth: Double,
            enemyCount: Int,
            enemyType: String,
            combatId: String,
            fightLog: ArrayList<String>
        ): MudFightFragment {
            val fragment = MudFightFragment()
            val args = Bundle()
            args.putDouble("enemy_health", enemyHealth)
            args.putInt("enemy_count", enemyCount)
            args.putString("enemy_type", enemyType)
            args.putString("combat_id", combatId)
            args.putStringArrayList("fight_log", fightLog)
            fragment.arguments = args
            return fragment
        }
    }
}
