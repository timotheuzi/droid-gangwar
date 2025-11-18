package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidgangwar.R
import com.example.droidgangwar.databinding.FragmentCityBinding

class CityFragment : Fragment() {

    private var _binding: FragmentCityBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe game state
        gameViewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            updateUI(gameState)
        }

        // Set up location buttons with beautiful animations
        setupLocationButtons()
    }

    private fun updateUI(gameState: com.example.droidgangwar.model.GameState) {
        binding.apply {
            // Update status display
            moneyText.text = "$${gameState.money}"
            healthText.text = "${gameState.health}/${gameState.maxHealth}"
            dayText.text = "Day ${gameState.day}"
            gangSizeText.text = "${gameState.members} members"

            // Update progress bars
            healthProgress.progress = (gameState.health.toFloat() / gameState.maxHealth * 100).toInt()
            dayProgress.progress = (gameState.steps.toFloat() / gameState.maxSteps * 100).toInt()

            // Show final battle button if ready
            finalBattleButton.visibility = if (gameState.members >= 10) View.VISIBLE else View.GONE
        }
    }

    private fun setupLocationButtons() {
        binding.apply {
            // Crackhouse - Drug dealing
            crackhouseCard.setOnClickListener {
                animateCardClick(it)
                gameViewModel.navigateToScreen("crackhouse")
            }

            // Gun Shack - Weapons
            gunshackCard.setOnClickListener {
                animateCardClick(it)
                gameViewModel.navigateToScreen("gunshack")
            }

            // Bank - Money management
            bankCard.setOnClickListener {
                animateCardClick(it)
                gameViewModel.navigateToScreen("bank")
            }

            // Bar - Information and contacts
            barCard.setOnClickListener {
                animateCardClick(it)
                gameViewModel.navigateToScreen("bar")
            }

            // Info Booth - Special items
            infoboothCard.setOnClickListener {
                animateCardClick(it)
                gameViewModel.navigateToScreen("infobooth")
            }

            // Alleyway - Exploration
            alleywayCard.setOnClickListener {
                animateCardClick(it)
                gameViewModel.navigateToScreen("alleyway")
            }

            // Pick n Save - Gang management
            picknsaveCard.setOnClickListener {
                animateCardClick(it)
                gameViewModel.navigateToScreen("picknsave")
            }

            // Wander the streets
            wanderButton.setOnClickListener {
                val result = gameViewModel.wander()
                showWanderResult(result)
            }

            // Final battle (only visible when ready)
            finalBattleButton.setOnClickListener {
                animateCardClick(it)
                gameViewModel.navigateToScreen("final_battle")
            }
        }
    }

    private fun animateCardClick(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun showWanderResult(result: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Street Exploration")
            .setMessage(result)
            .setPositiveButton("Continue", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
