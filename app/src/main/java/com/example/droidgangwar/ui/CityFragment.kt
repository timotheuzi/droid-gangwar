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

            // Credits - Game information
            creditsCard.setOnClickListener {
                animateCardClick(it)
                showCreditsDialog()
            }

            // Wander the streets
            wanderButton.setOnClickListener {
                performWanderExploration()
            }

            // Final battle (only visible when ready)
            finalBattleButton.setOnClickListener {
                animateCardClick(it)
                gameViewModel.navigateToScreen("final_battle")
            }
        }
    }

    private fun showCreditsDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Game Credits")
            .setMessage("Droid Gangwar - A mobile adaptation of the classic Gang War MUD game.\n\n" +
                    "Original Game: Gang War MUD by Timothy Zieman\n" +
                    "Android Adaptation: Built with Android Studio\n" +
                    "\nSpecial Thanks:\n" +
                    "• Original Gang War community\n" +
                    "• Android development team\n" +
                    "• All beta testers\n\n" +
                    "Version 1.0\n" +
                    "Built with ❤️ for gang warfare enthusiasts")
            .setPositiveButton("Back to City") { _, _ ->
                // Just close the dialog
            }
            .setCancelable(false)
            .show()
    }

    private fun showScreenForLocation(screen: String) {
        when (screen) {
            "city" -> showCityScreen()
            else -> {
                // For other screens, the navigation is handled by MainActivity
            }
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
            creditsCard.visibility = View.VISIBLE
            wanderButton.visibility = View.VISIBLE
        }
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

    private fun performWanderExploration() {
        val gameState = gameViewModel.gameState.value ?: return

        // Generate random exploration event
        val exploreEvent = com.example.droidgangwar.data.RandomEventData.generateWanderingEvent(gameState)

        // Check if event meets requirements
        if (com.example.droidgangwar.data.RandomEventData.hasMeetRequirements(exploreEvent, gameState)) {
            // Apply event effects
            com.example.droidgangwar.data.RandomEventData.applyEventEffects(exploreEvent, gameState)

            // Handle combat events
            when (exploreEvent.type) {
                com.example.droidgangwar.model.EventType.GANG_FIGHT -> {
                    startMudFight("Gang Members", (2..6).random(), "Rival gang ambushes you while wandering!")
                }
                com.example.droidgangwar.model.EventType.POLICE_CHASE -> {
                    startMudFight("Police Officers", (3..5).random(), "Police patrol spots you!")
                }
                com.example.droidgangwar.model.EventType.SQUIDIE_HIT_SQUAD -> {
                    startMudFight("Squidie Hit Squad", (2..4).random(), "Squidie assassins track you down!")
                }
                else -> {
                    // Regular event - show results
                    showWanderResult(exploreEvent.description)
                }
            }

            // Update game state and increment steps
            gameViewModel.updateGameState(gameState)
            gameViewModel.incrementSteps()
        }
    }

    private fun startMudFight(enemyType: String, enemyCount: Int, initialMessage: String) {
        val enemyHealth = when (enemyType) {
            "Police Officers" -> enemyCount * 12
            "Gang Members" -> enemyCount * 15
            "Squidie Hit Squad" -> enemyCount * 20
            else -> enemyCount * 15
        }

        val combatId = "city_wander_combat_${System.currentTimeMillis()}"
        val initialLog = arrayListOf(initialMessage)

        // Navigate to MudFightFragment with combat parameters via ViewModel
        gameViewModel.startMudFight(enemyHealth, enemyCount, enemyType, combatId, initialLog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

}
}
