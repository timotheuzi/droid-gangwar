package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.appcompat.app.AlertDialog
import com.example.droidgangwar.data.HighScoreRepository
import com.example.droidgangwar.databinding.FragmentInfoboothBinding

class InfoBoothFragment : Fragment() {

    private var _binding: FragmentInfoboothBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var highScoreRepository: HighScoreRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoboothBinding.inflate(inflater, container, false)
        highScoreRepository = HighScoreRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe game state
        gameViewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            updateUI(gameState)
        }

        setupInfoBoothActions()
    }

    private fun updateUI(gameState: com.example.droidgangwar.model.GameState) {
        binding.apply {
            moneyText.text = "$${gameState.money}"
            healthText.text = "${gameState.health}/${gameState.maxHealth}"
            gangSizeText.text = "${gameState.members} members"
            dayText.text = "Day ${gameState.day}"
            stepsText.text = "${gameState.steps}/${gameState.maxSteps} steps"
        }
    }

    private fun setupInfoBoothActions() {
        binding.apply {
            checkStatsButton.setOnClickListener {
                showStats()
            }

            buySpecialItemsButton.setOnClickListener {
                buySpecialItems()
            }

            viewPricesButton.setOnClickListener {
                viewCurrentPrices()
            }
            
            highScoresButton.setOnClickListener {
                showHighScores()
            }

            backButton.setOnClickListener {
                gameViewModel.navigateToScreen("city")
            }
        }
    }

    private fun showStats() {
        val gameState = gameViewModel.gameState.value ?: return
        gameState.updateCurrentScore()
        
        // Check if current score qualifies for high score list
        highScoreRepository.addHighScore(gameState.playerName.ifEmpty { "You" }, gameState.currentScore)
        
        val stats = """
            Character Stats:
            Player: ${gameState.playerName}
            Gang: ${gameState.gangName}
            Current Score: ${gameState.currentScore}
            
            Money: $${gameState.money}
            Health: ${gameState.health}/${gameState.maxHealth}
            Gang Members: ${gameState.members}
            Day: ${gameState.day}
            Steps Taken: ${gameState.steps}/${gameState.maxSteps}
            Account: $${gameState.account}
            Loan: $${gameState.loan}
        """.trimIndent()
        
        showMessage("Your Status", stats)
    }
    
    private fun showHighScores() {
        val highScores = highScoreRepository.getHighScores()
        val sb = StringBuilder()
        
        highScores.forEachIndexed { index, score ->
            sb.append("${index + 1}. ${score.name}: ${score.score}\n")
        }
        
        val gameState = gameViewModel.gameState.value
        if (gameState != null) {
            gameState.updateCurrentScore()
            sb.append("\nYour Current Score: ${gameState.currentScore}")
        }
        
        showMessage("High Scores", sb.toString())
    }

    private fun buySpecialItems() {
        showMessage("Special Items", "Special items coming soon!")
    }

    private fun viewCurrentPrices() {
        val gameState = gameViewModel.gameState.value ?: return
        
        val prices = """
            Weed: $${gameState.drugPrices["weed"] ?: 50}
            Crack: $${gameState.drugPrices["crack"] ?: 1000}
            Cocaine: $${gameState.drugPrices["coke"] ?: 8000}
            Ice: $${gameState.drugPrices["ice"] ?: 5000}
            Percs: $${gameState.drugPrices["percs"] ?: 20000}
            Pixie Dust: $${gameState.drugPrices["pixie_dust"] ?: 100000}
        """.trimIndent()
        
        showMessage("Current Drug Prices", prices)
    }

    private fun showMessage(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
