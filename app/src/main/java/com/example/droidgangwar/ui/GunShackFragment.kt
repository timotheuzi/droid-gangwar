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
