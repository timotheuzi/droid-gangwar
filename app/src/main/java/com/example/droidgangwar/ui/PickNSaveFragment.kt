package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidgangwar.R
import com.example.droidgangwar.databinding.FragmentPicknsaveBinding

class PickNSaveFragment : Fragment() {

    private var _binding: FragmentPicknsaveBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPicknsaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe game state
        gameViewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            updateUI(gameState)
        }

        setupPickNSaveActions()
    }

    private fun updateUI(gameState: com.example.droidgangwar.model.GameState) {
        binding.apply {
            moneyText.text = "$${gameState.money}"
            gangMembersText.text = "${gameState.members} members"
            healthText.text = "${gameState.health}/${gameState.maxHealth}"
        }
    }

    private fun setupPickNSaveActions() {
        binding.apply {
            recruitButton.setOnClickListener {
                recruitMember()
            }

            trainButton.setOnClickListener {
                trainGang()
            }

            healButton.setOnClickListener {
                healGang()
            }

            searchStoreButton.setOnClickListener {
                searchStore()
            }

            backButton.setOnClickListener {
                gameViewModel.navigateToScreen("city")
            }
        }
    }

    private fun recruitMember() {
        val gameState = gameViewModel.gameState.value ?: return
        
        if (gameState.money >= 1000) {
            gameState.money -= 1000
            gameState.members += 1
            gameViewModel.saveGameState()
            updateUI(gameState)
            showMessage("You successfully recruited a new gang member!")
        } else {
            showMessage("Not enough money to recruit! Need $1,000")
        }
    }

    private fun trainGang() {
        val gameState = gameViewModel.gameState.value ?: return
        
        if (gameState.money >= 500) {
            gameState.money -= 500
            gameViewModel.saveGameState()
            updateUI(gameState)
            showMessage("Your gang has been trained! They are now more effective in combat.")
        } else {
            showMessage("Not enough money to train! Need $500")
        }
    }

    private fun healGang() {
        val gameState = gameViewModel.gameState.value ?: return
        
        val healCost = 100
        if (gameState.money >= healCost) {
            gameState.money -= healCost
            gameState.health = minOf(gameState.health + 20, gameState.maxHealth)
            gameViewModel.saveGameState()
            updateUI(gameState)
            showMessage("Your gang has been healed!")
        } else {
            showMessage("Not enough money to heal! Need $$healCost")
        }
    }

    private fun searchStore() {
        val gameState = gameViewModel.gameState.value ?: return
        
        val searchResults = listOf(
            "You find $500 in the cash register",
            "You discover hidden supplies worth $300",
            "You find nothing of value",
            "You find some medical supplies",
            "You accidentally trigger the alarm - better leave quickly!"
        )
        
        val result = searchResults.random()
        showMessage("Store Search:\n\n$result")
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
