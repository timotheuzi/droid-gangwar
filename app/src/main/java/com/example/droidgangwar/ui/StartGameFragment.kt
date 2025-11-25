package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.droidgangwar.R

class StartGameFragment : Fragment() {

    private var _binding: com.example.droidgangwar.databinding.FragmentStartGameBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.example.droidgangwar.databinding.FragmentStartGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupStartGameButton()
    }

    private fun setupStartGameButton() {
        binding.startGameButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString().trim()
            val gangName = binding.gangNameInput.text.toString().trim()

            if (playerName.isEmpty()) {
                binding.playerNameInput.error = "Please enter your name"
                return@setOnClickListener
            }

            if (gangName.isEmpty()) {
                binding.gangNameInput.error = "Please enter your gang name"
                return@setOnClickListener
            }

            // Update game state with user names
            val currentGameState = gameViewModel.gameState.value ?: return@setOnClickListener
            currentGameState.playerName = playerName
            currentGameState.gangName = gangName
            gameViewModel.updateGameState(currentGameState)

            // Navigate to the city
            findNavController().navigate(R.id.nav_city)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
