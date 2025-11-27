package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.appcompat.app.AlertDialog
import com.example.droidgangwar.R
import com.example.droidgangwar.databinding.FragmentCityBinding
import com.example.droidgangwar.data.RandomEventData
import com.example.droidgangwar.model.EventType
import com.example.droidgangwar.model.GameState
import kotlin.random.Random

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

    private fun updateUI(gameState: GameState) {
        binding.apply {
            // Update status display with player names
            val playerNameText = gameState.playerName.ifEmpty { "Unknown" }
            val gangNameText = gameState.gangName.ifEmpty { "No Gang" }
            playerNameTextView.text = playerNameText
            gangNameTextView.text = gangNameText
            healthText.text = "${gameState.health}/${gameState.maxHealth}"
            dayText.text = "Day ${gameState.day}"

            // Update progress bars
            healthProgress.progress = (gameState.health.toFloat() / gameState.maxHealth * 100).toInt()
            dayProgress.progress = (gameState.steps.toFloat() / gameState.maxSteps * 100).toInt()

            // Show final battle button if ready
            finalBattleButton.visibility = if (gameState.members >= 10) View.VISIBLE else View.GONE
            
            // Update Drug Prices on Main Screen
            updateDrugPrices(gameState)
        }
    }

    private fun updateDrugPrices(gameState: GameState) {
        binding.apply {
            priceWeed.text = "Weed: $${gameState.drugPrices["weed"] ?: 0}/kg"
            priceIce.text = "Ice: $${gameState.drugPrices["ice"] ?: 0}/kg"
            priceCrack.text = "Crack: $${gameState.drugPrices["crack"] ?: 0}/kg"
            pricePercs.text = "Percs: $${gameState.drugPrices["percs"] ?: 0}/kg"
            priceCoke.text = "Cocaine: $${gameState.drugPrices["coke"] ?: 0}/kg"
            pricePixie.text = "Pixie Dust: $${gameState.drugPrices["pixie_dust"] ?: 0}/kg"
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
        AlertDialog.Builder(requireContext())
            .setTitle("Game Credits")
            .setMessage("Droid Gangwar - A mobile adaptation of the classic Gang War MUD game.\n\n" +
                    "Original Game: Gang War MUD by timotheuzi@hotmail.com\n" +
                    "Android Adaptation: Built with Android Studio\n\n" +
                    "Created and Maintained by timotheuzi@hotmail.com\n\n" +
                    "Public Address to Receive:\n" +
                    "LTC ltc1qcx3xsrpxqm7q7gpkxhxhtaeqgdqpmq0jdrw7vh\n" +
                    "SOL 4sAaizpXmFS4yedakv7mLN1Z2myGh2CWnes3YJBhF1Hb\n" +
                    "XLM GCVYEJ7GC7LZZ2EBZL5DXWCLTZPTXX7YEUXLS36YGE6BA37R5BHRI2XG\n" +
                    "BTC bc1qfv69rux98r7u3sr786j2qpsenmkskvkf58ynkk\n" +
                    "ETH 0xD1A6b95958dE597c2D9478A3b4212adF0789BF81\n\n" +
                    "Special Thanks:\n" +
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
        AlertDialog.Builder(requireContext())
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
        AlertDialog.Builder(requireContext())
            .setTitle("Street Exploration")
            .setMessage(result)
            .setPositiveButton("Continue", null)
            .show()
    }

    private fun performWanderExploration() {
        val gameState = gameViewModel.gameState.value ?: return

        // Check if we should run a price fluctuation event
        // 10% chance during wander to hear market rumors which forces an update without day change
        // Or simply notify if prices changed recently? The prompt asked for notification on custom events.
        
        // Generate random exploration event
        val exploreEvent = RandomEventData.generateRandomEvent(gameState)

        // Check if event meets requirements
        if (RandomEventData.hasMeetRequirements(exploreEvent, gameState)) {
            // Apply event effects
            RandomEventData.applyEventEffects(exploreEvent, gameState)

            // Handle combat events
            when (exploreEvent.type) {
                EventType.GANG_FIGHT -> {
                    startMudFight("Gang Members", (2..6).random(), "Rival gang ambushes you while wandering!")
                }
                EventType.POLICE_CHASE -> {
                    startMudFight("Police Officers", (3..5).random(), "Police patrol spots you!")
                }
                EventType.SQUIDIE_HIT_SQUAD -> {
                    startMudFight("Squidie Hit Squad", (2..4).random(), "Squidie assassins track you down!")
                }
                EventType.NPC_ENCOUNTER -> {
                     // Chance for combat with monster or weirdo
                     if (Random.nextFloat() < 0.2f) {
                          startMudFight("Sewer Monster", 1, "A Sewer Monster emerges from the drain!")
                     } else {
                          showWanderResult(exploreEvent.description)
                     }
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
            "Sewer Monster" -> 50 // Strong single enemy
            else -> enemyCount * 15
        }

        val combatId = "city_wander_combat_${System.currentTimeMillis()}"
        
        // Navigate to MudFightFragment with combat parameters via ViewModel
        gameViewModel.startMudFight(enemyHealth, enemyCount, enemyType, combatId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
