package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidgangwar.R
import com.example.droidgangwar.databinding.FragmentGunshackBinding

class GunShackFragment : Fragment() {

    private var _binding: FragmentGunshackBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGunshackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe game state
        gameViewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            updateUI(gameState)
        }

        setupGunShackActions()
    }

    private fun updateUI(gameState: com.example.droidgangwar.model.GameState) {
        binding.apply {
            moneyText.text = "$${gameState.money}"
        }
    }

    private fun setupGunShackActions() {
        binding.apply {
            // Weapon purchase buttons
            pistolButton.setOnClickListener {
                purchaseWeapon("pistol", 1200)
            }
            
            ghostGunButton.setOnClickListener {
                purchaseWeapon("ghost_gun", 2000)
            }
            
            bulletsButton.setOnClickListener {
                purchaseWeapon("bullets", 100)
            }

            explodingBulletsButton.setOnClickListener {
                purchaseWeapon("exploding_bullets", 2000)
            }

            grenadeButton.setOnClickListener {
                purchaseWeapon("grenade", 1000)
            }

            // Body armor buttons
            lightVestButton.setOnClickListener {
                purchaseWeapon("light_vest", 5000)
            }
            
            mediumVestButton.setOnClickListener {
                purchaseWeapon("medium_vest", 25000)
            }
            
            heavyVestButton.setOnClickListener {
                purchaseWeapon("heavy_vest", 35000)
            }

            // Switch purchase button
            switchButton.setOnClickListener {
                if (hasPistolOrGhostGun()) {
                    purchaseSwitch()
                } else {
                    showMessage("You need a pistol or ghost gun first!")
                }
            }

            // Pistol upgrade buttons
            pistolDamageUpgradeButton.setOnClickListener {
                if (hasPistol()) {
                    purchasePistolUpgrade("damage", 5000)
                } else {
                    showMessage("You need a pistol first!")
                }
            }

            pistolAccuracyUpgradeButton.setOnClickListener {
                if (hasPistol()) {
                    purchasePistolUpgrade("accuracy", 4500)
                } else {
                    showMessage("You need a pistol first!")
                }
            }

            pistolMagazineUpgradeButton.setOnClickListener {
                if (hasPistol()) {
                    purchasePistolUpgrade("magazine", 3000)
                } else {
                    showMessage("You need a pistol first!")
                }
            }

            backButton.setOnClickListener {
                gameViewModel.navigateToScreen("city")
            }
        }
    }

    private fun purchaseWeapon(weaponType: String, price: Int) {
        val gameState = gameViewModel.gameState.value ?: return
        
        if (gameState.money >= price) {
            gameState.money -= price
            
            when (weaponType) {
                "pistol" -> gameState.weapons.pistols += 1
                "ghost_gun" -> gameState.weapons.ghostGuns += 1
                "bullets" -> gameState.weapons.bullets += 50
                "exploding_bullets" -> gameState.weapons.bullets += 50
                "grenade" -> gameState.weapons.grenades += 1
                "light_vest" -> gameState.weapons.vest = 5
                "medium_vest" -> gameState.weapons.vest = 10
                "heavy_vest" -> gameState.weapons.vest = 15
            }
            
            gameViewModel.saveGameState()
            updateUI(gameState)
            showMessage("Purchased $weaponType for $$price!")
        } else {
            showMessage("Not enough money! Need $$price")
        }
    }

    private fun purchaseSwitch() {
        val gameState = gameViewModel.gameState.value ?: return
        val switchPrice = 3000
        
        if (gameState.money >= switchPrice) {
            if (!gameState.flags.hasSwitch) {
                gameState.money -= switchPrice
                gameState.flags.hasSwitch = true
                gameViewModel.saveGameState()
                updateUI(gameState)
                showMessage("Purchased switch for $$switchPrice! Now you get 3 shots per turn with pistol/ghost gun!")
            } else {
                showMessage("You already have a switch installed!")
            }
        } else {
            showMessage("Not enough money! Need $$switchPrice for a switch")
        }
    }

    private fun hasPistol(): Boolean {
        val gameState = gameViewModel.gameState.value ?: return false
        return gameState.weapons.pistols > 0
    }

    private fun hasPistolOrGhostGun(): Boolean {
        val gameState = gameViewModel.gameState.value ?: return false
        return gameState.weapons.pistols > 0 || gameState.weapons.ghostGuns > 0
    }

    private fun purchasePistolUpgrade(upgradeType: String, price: Int) {
        val gameState = gameViewModel.gameState.value ?: return

        // Check if player can afford
        if (gameState.money < price) {
            showMessage("Not enough money! Need $$price")
            return
        }

        // Check if they already have an upgrade
        if (gameState.pistolUpgradeType != "none") {
            showMessage("You already have a pistol upgrade! Can only have one type.")
            return
        }

        // Purchase upgrade
        gameState.money -= price
        gameState.pistolUpgradeType = upgradeType

        val upgradeNames = mapOf(
            "damage" to "Pistol Damage Upgrade (+50% damage)",
            "accuracy" to "Pistol Accuracy Upgrade (+5 damage)",
            "magazine" to "Pistol Magazine Upgrade (+50% ammo capacity)"
        )

        gameViewModel.saveGameState()
        updateUI(gameState)
        showMessage("Purchased ${upgradeNames[upgradeType] ?: "pistol upgrade"} for $$price!")
    }

    private fun showMessage(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
