package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidgangwar.databinding.FragmentStartGameBinding

class StartGameFragment : Fragment() {

    private var _binding: FragmentStartGameBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartGameBinding.inflate(inflater, container, false)
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

            // Start a new game which updates state and sets screen to 'city'
            gameViewModel.startNewGame(playerName, gangName)
            // Navigation is handled by observing currentScreen in MainActivity
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
