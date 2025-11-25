package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidgangwar.R
import com.example.droidgangwar.databinding.FragmentInfoboothBinding

class InfoBoothFragment : Fragment() {

    private var _binding: FragmentInfoboothBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoboothBinding.inflate(inflater, container, false)
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

            backButton.setOnClickListener {
                gameViewModel.navigateToScreen("city")
            }
        }
    }

    private fun showStats() {
        val gameState = gameViewModel.gameState.value ?: return
        
        val stats = """
            Character Stats:
            Money: $${gameState.money}
            Health: ${gameState.health}/${gameState.maxHealth}
            Gang Members: ${gameState.members}
            Day: ${gameState.day}
            Steps Taken: ${gameState.steps}/${gameState.maxSteps}
            Account: $${gameState.account}
            Loan: $${gameState.loan}
        """.trimIndent()
        
        showMessage(stats)
    }

    private fun buySpecialItems() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Special Items")
            .setMessage("Special items coming soon!")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun viewCurrentPrices() {
        val gameState = gameViewModel.gameState.value ?: return
        
        val prices = """
            Current Drug Prices:
            Weed: $${gameState.drugPrices["weed"] ?: 50}
            Crack: $${gameState.drugPrices["crack"] ?: 1000}
            Cocaine: $${gameState.drugPrices["coke"] ?: 8000}
            Ice: $${gameState.drugPrices["ice"] ?: 5000}
            Percs: $${gameState.drugPrices["percs"] ?: 20000}
            Pixie Dust: $${gameState.drugPrices["pixie_dust"] ?: 100000}
        """.trimIndent()
        
        showMessage(prices)
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
