package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.appcompat.app.AlertDialog

import com.example.droidgangwar.databinding.FragmentMudFightBinding
import com.example.droidgangwar.model.CombatSystem
import com.example.droidgangwar.ui.GameViewModel
import java.util.Locale

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

        // Get combat parameters from ViewModel
        gameViewModel.currentCombatData?.let { combatData ->
            enemyHealth = combatData.enemyHealth
            enemyCount = combatData.enemyCount
            enemyType = combatData.enemyType
            combatId = combatData.combatId
            if (fightLog.isEmpty()) {
                fightLog.add(combatData.initialMessage)
            }
        } ?: run {
            // Fallback if no combat data available
            enemyHealth = 30.0
            enemyCount = 1
            enemyType = "Enemy"
            combatId = "combat_${System.currentTimeMillis()}"
            if (fightLog.isEmpty()) {
                fightLog.add("Combat begins!")
            }
        }

        setupUI()
        setupWeaponSpinner()
        setupAmmoSpinner()
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
        val weaponDisplayNames = mutableListOf<String>()

        // Add available weapons based on inventory
        if (gameState.weapons.canFightWithPistol()) {
            availableWeapons.add("pistol")
            weaponDisplayNames.add("Pistol")
        }
        if (gameState.weapons.ghostGuns > 0 && gameState.weapons.bullets > 0) {
            availableWeapons.add("ghost_gun")
            weaponDisplayNames.add("Ghost Gun")
        }
        if (gameState.weapons.canFightWithUzi()) {
            availableWeapons.add("uzi")
            weaponDisplayNames.add("Uzi")
        }
        if (gameState.weapons.canFightWithGrenade()) {
            availableWeapons.add("grenade")
            weaponDisplayNames.add("Grenade")
        }
        if (gameState.weapons.canFightWithMissile()) {
            availableWeapons.add("missile_launcher")
            weaponDisplayNames.add("Missile Launcher")
        }
        if (gameState.weapons.barbedWireBat > 0) {
            availableWeapons.add("barbed_wire_bat")
            weaponDisplayNames.add("Barbed Wire Bat")
        }
        if (gameState.weapons.brassKnuckles > 0) {
            availableWeapons.add("brass_knuckles")
            weaponDisplayNames.add("Brass Knuckles")
        }
        // Knife is always available
        availableWeapons.add("knife")
        weaponDisplayNames.add("Knife")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weaponDisplayNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.weaponSpinner.adapter = adapter
        
        // Tag the spinner so we can retrieve the internal weapon key
        binding.weaponSpinner.tag = availableWeapons
    }
    
    private fun setupAmmoSpinner() {
        val gameState = gameViewModel.gameState.value ?: return
        val ammoTypes = mutableListOf("Standard Ammo")
        
        if (gameState.weapons.explodingBullets > 0) {
            ammoTypes.add("Exploding Ammo")
        }
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ammoTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        // Assuming there's an ammo spinner or we repurpose/add one.
        // The previous implementation used a Switch. The prompt asks for bullet selection.
        // A Spinner is more extensible. But wait, the XML only has a Switch (which I added in previous steps).
        // I should either use the Switch (and rename/update logic) or replace it with a Spinner in XML.
        // Given the prompt "select other bullets besides the default ones", a Switch works for 2 types.
        // If there were more, a Spinner would be needed.
        // However, the prompt implies the user COULD NOT use them.
        // Let's stick to the Switch for now as it's already in XML, but ensure it actually WORKS.
        // In `setupActionButtons`, I see the listener `gameViewModel.toggleExplodingBullets(isChecked)`.
        // This updates the ViewModel state. 
        // The `CombatSystem` checks `gameState.weapons.useExplodingBullets`.
        
        // The issue might be that the UI state (Switch) isn't reflecting the saved state correctly or
        // the ViewModel isn't persisting it properly during combat turns.
        
        // Or maybe the prompt wants a Spinner because a Switch feels like a "setting" rather than a "selection".
        // But let's check `setupActionButtons` again.
        
        // I will make sure the Switch is visible and its state is synced.
        // Actually, if I want to be robust, I'll ensure the Switch is checked if `useExplodingBullets` is true.
    }

    private fun setupActionButtons() {
        binding.apply {
            attackButton.setOnClickListener { performAttack() }
            defendButton.setOnClickListener { performDefend() }
            fleeButton.setOnClickListener { performFlee() }
            useDrugButton.setOnClickListener { showDrugSelection() }
            backButton.setOnClickListener { navigateBackToCity() }
            
            // Add ammo toggle if they have exploding bullets
            val gameState = gameViewModel.gameState.value
            if (gameState != null && gameState.weapons.explodingBullets > 0) {
                explodingAmmoSwitch.visibility = View.VISIBLE
                explodingAmmoSwitch.isChecked = gameState.weapons.useExplodingBullets
                explodingAmmoSwitch.text = "Exploding Ammo (${gameState.weapons.explodingBullets})"
                
                // Ensure logic updates immediately
                explodingAmmoSwitch.setOnCheckedChangeListener { _, isChecked ->
                    gameViewModel.toggleExplodingBullets(isChecked)
                    // Also update UI text immediately if count changes later?
                    // No, count changes on attack.
                }
            } else {
                explodingAmmoSwitch.visibility = View.GONE
            }
        }
    }

    private fun performAttack() {
        val gameState = gameViewModel.gameState.value ?: return
        val selectedWeapon = getSelectedWeapon()
        
        // Ensure ammo preference is current
        if (binding.explodingAmmoSwitch.visibility == View.VISIBLE) {
             gameState.weapons.useExplodingBullets = binding.explodingAmmoSwitch.isChecked
        }

        // Perform combat
        val result = CombatSystem.calculateCombat(gameState, selectedWeapon, enemyType, enemyCount, enemyHealth)

        // Update fight log
        fightLog.addAll(result.fightLog)

        // Update enemy health and count
        enemyHealth -= result.damageDealt
        enemyCount -= result.enemiesKilled
        if (enemyCount < 0) enemyCount = 0

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

        // Reduced enemy damage logic could go here or in CombatSystem
        // For now let's just say you brace yourself.
        fightLog.add("You brace for impact!")
        
        // Enemy still attacks
        if (enemyCount > 0) {
             val damage = (enemyCount * 2..enemyCount * 5).random()
             gameState.takeDamage(damage)
             fightLog.add("The $enemyType attack! You take $damage damage.")
             
             if (gameState.health <= 0) {
                 handleDefeat()
             }
        }

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
            if (enemyCount > 0) {
                 val damage = (enemyCount * 3..enemyCount * 8).random()
                 gameState.takeDamage(damage)
                 fightLog.add("The $enemyType attack while you are distracted! You take $damage damage.")
                 
                 if (gameState.health <= 0) {
                     handleDefeat()
                 }
            }
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
                "crack" -> "Crack (damage, boost)"
                "percs" -> "Percs (heal)"
                else -> it
            }
        }

        AlertDialog.Builder(requireContext())
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
             // Note: CombatSystem updates the counts in the gameState object passed to it?
             // Let's verify. Yes, we added that.
        }
        updateUI()
        gameViewModel.saveGameState()
    }

    private fun getSelectedWeapon(): String {
        val selectedPosition = binding.weaponSpinner.selectedItemPosition
        @Suppress("UNCHECKED_CAST")
        val availableWeapons = binding.weaponSpinner.tag as? List<String>
        
        return availableWeapons?.getOrElse(selectedPosition) { "knife" } ?: "knife"
    }

    private fun handleVictory() {
        fightLog.add("🎉 COMBAT VICTORY! 🎉")
        updateUI()

        // Show victory dialog
        AlertDialog.Builder(requireContext())
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

        fightLog.add("💀 YOU ARE DEAD 💀")
        updateUI()
        
        // Game over
        AlertDialog.Builder(requireContext())
            .setTitle("Game Over")
            .setMessage("You have been defeated! Your journey ends here.")
            .setPositiveButton("Return to Main Menu") { _, _ ->
                // Navigate to game over or main menu
                gameViewModel.navigateToScreen("main_menu")
            }
            .setCancelable(false)
            .show()
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
            enemyHealthText.text = "Health: %.1f".format(Locale.US, enemyHealth)

            // Update weapon ammo counts
            ammoText.text = "Bullets: ${gameState.weapons.bullets}, Exploding: ${gameState.weapons.explodingBullets}"
            
            // Update exploding ammo switch text if visible
            if (explodingAmmoSwitch.visibility == View.VISIBLE) {
                explodingAmmoSwitch.text = "Exploding Ammo (${gameState.weapons.explodingBullets})"
                // Ensure checked state matches current logical state
                // But be careful not to loop if we set listener first.
                // Actually, checking it programmatically might trigger listener.
                // It's safer to just rely on the listener for updates, and init once.
                // But if bullets run out, we might want to disable it.
                if (gameState.weapons.explodingBullets <= 0) {
                    explodingAmmoSwitch.isChecked = false
                    explodingAmmoSwitch.isEnabled = false
                } else {
                    explodingAmmoSwitch.isEnabled = true
                }
            }

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
}
