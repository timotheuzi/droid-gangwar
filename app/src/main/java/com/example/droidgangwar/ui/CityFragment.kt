package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

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

        // Observe current screen to show appropriate UI
        gameViewModel.currentScreen.observe(viewLifecycleOwner) { screen ->
            showScreenForLocation(screen)
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

    private fun showScreenForLocation(screen: String) {
        when (screen) {
            "city" -> showCityScreen()
            "crackhouse" -> showCrackhouseScreen()
            "gunshack" -> showGunshackScreen()
            "bank" -> showBankScreen()
            "bar" -> showBarScreen()
            "infobooth" -> showInfoboothScreen()
            "alleyway" -> showAlleywayScreen()
            "picknsave" -> showPicknsaveScreen()
            "final_battle" -> showFinalBattleScreen()
            else -> showCityScreen()
        }
    }

    private fun showCityScreen() {
        binding.apply {
            // Show explore title and location grid
            (binding.root.findViewById<View>(com.example.droidgangwar.R.id.exploreTitle) as? TextView)?.visibility = View.VISIBLE
            // Show all location cards
            crackhouseCard.visibility = View.VISIBLE
            gunshackCard.visibility = View.VISIBLE
            bankCard.visibility = View.VISIBLE
            barCard.visibility = View.VISIBLE
            infoboothCard.visibility = View.VISIBLE
            alleywayCard.visibility = View.VISIBLE
            picknsaveCard.visibility = View.VISIBLE
            wanderButton.visibility = View.VISIBLE
        }
    }

    private fun showCrackhouseScreen() {
        showLocationDialog("Crackhouse", "Welcome to the Crackhouse!\n\nHere you can buy and sell drugs.")
    }

    private fun showGunshackScreen() {
        showLocationDialog("Gun Shack", "Welcome to the Gun Shack!\n\nHere you can buy weapons and ammo.")
    }

    private fun showBankScreen() {
        showLocationDialog("Bank", "Welcome to the Bank!\n\nHere you can manage your money.")
    }

    private fun showBarScreen() {
        showLocationDialog("Bar", "Welcome to the Bar!\n\nHere you can meet contacts and get information.")
    }

    private fun showInfoboothScreen() {
        showLocationDialog("Info Booth", "Welcome to the Info Booth!\n\nHere you can buy special items.")
    }

    private fun showAlleywayScreen() {
        showLocationDialog("Alleyway", "Welcome to the Alleyway!\n\nDangerous place for exploration and combat.")
    }

    private fun showPicknsaveScreen() {
        showLocationDialog("Pick n Save", "Welcome to Pick n Save!\n\nHere you can manage your gang.")
    }

    private fun showFinalBattleScreen() {
        showLocationDialog("Final Battle", "Time for the FINAL BATTLE!\n\nDestroy the Squidies!")
    }

    private fun showLocationDialog(title: String, message: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Back to City") { _, _ ->
                gameViewModel.navigateToScreen("city")
            }
            .setCancelable(false)
            .show()
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
