package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidgangwar.R
import com.example.droidgangwar.databinding.FragmentBarBinding

class BarFragment : Fragment() {

    private var _binding: FragmentBarBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe game state
        gameViewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            updateUI(gameState)
        }

        setupBarActions()
    }

    private fun updateUI(gameState: com.example.droidgangwar.model.GameState) {
        binding.apply {
            moneyText.text = "$${gameState.money}"
            // Simplified contact status
            noxContactText.text = "Not met yet"
            razeContactText.text = "Not met yet"
            
            // Update market info
            weedMarketText.text = "Weed market: Stable"
            crackMarketText.text = "Crack market: Cool"
            cokeMarketText.text = "Cocaine market: Stable"
        }
    }

    private fun setupBarActions() {
        binding.apply {
            // Meet contacts
            noxButton.setOnClickListener {
                meetContact("nox")
            }
            
            razeButton.setOnClickListener {
                meetContact("raze")
            }

            // Talk to contacts (only visible when met)
            noxTalkButton.setOnClickListener {
                talkToNpc("nox")
            }
            
            razeTalkButton.setOnClickListener {
                talkToNpc("raze")
            }

            // Trade with contacts (only visible when met)
            noxTradeButton.setOnClickListener {
                tradeWithNpc("nox")
            }
            
            razeTradeButton.setOnClickListener {
                tradeWithNpc("raze")
            }

            // Look around button
            lookAroundButton.setOnClickListener {
                showMessage("You look around the bar. It seems quiet tonight.")
            }

            backButton.setOnClickListener {
                gameViewModel.navigateToScreen("city")
            }
        }
    }

    private fun meetContact(contactName: String) {
        val gameState = gameViewModel.gameState.value ?: return
        
        when (contactName) {
            "nox" -> {
                showMessage("You approach Nox, the informant. 'I can give you the street intel you need,' he says.")
            }
            "raze" -> {
                showMessage("You meet Raze, the supplier. 'I can get you better deals on bulk,' he promises.")
            }
        }
        
        gameViewModel.saveGameState()
        updateUI(gameState)
    }

    private fun talkToNpc(npcId: String) {
        val message = when (npcId) {
            "nox" -> "Nox: 'The Squidies are getting stronger. You need more firepower.'\n\nHe gives you intel about rival gang movements."
            "raze" -> "Raze: 'Prices are good right now. Stock up while you can.'\n\nHe offers you a discount on bulk purchases."
            else -> "The contact has nothing to say."
        }
        showMessage(message)
    }

    private fun tradeWithNpc(npcId: String) {
        val message = when (npcId) {
            "nox" -> "Nox offers you information about drug prices and upcoming raids."
            "raze" -> "Raze shows you his catalog of high-quality suppliers."
            else -> "No trade available."
        }
        showMessage(message)
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
