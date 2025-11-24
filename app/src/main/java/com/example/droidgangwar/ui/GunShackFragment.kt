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
            // Simplified weapons purchase buttons
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

            pistolUpgradeToggle.setOnClickListener {
                val gameState = gameViewModel.gameState.value ?: return@setOnClickListener
                if (gameState.pistolUpgraded) {
                    gameState.pistolUpgraded = false
                    pistolUpgradeToggle.text = "Enable Pistol Upgrade"
                    showMessage("Pistol upgrade disabled!")
                } else {
                    if (gameState.pistolUpgradeType != "none") {
                        gameState.pistolUpgraded = true
                        pistolUpgradeToggle.text = "Disable Pistol Upgrade"
                        showMessage("Pistol upgrade enabled!")
                    } else {
                        showMessage("Purchase an upgrade first!")
                    }
                }
                gameViewModel.saveGameState()

                updateUI(gameState)
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
            gameViewModel.saveGameState()
            updateUI(gameState)
            showMessage("Purchased $weaponType for $$price!")
        } else {
            showMessage("Not enough money! Need $$price")
        }
    }

    private fun hasPistol(): Boolean {
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
lateinit var pistolDamageUpgradeButton: android.widget.Button
lateinit var pistolAccuracyUpgradeButton: android.widget.Button
lateinit var pistolMagazineUpgradeButton: android.widget.Button
lateinit var pistolUpgradeToggle: android.widget.ToggleButton
}
