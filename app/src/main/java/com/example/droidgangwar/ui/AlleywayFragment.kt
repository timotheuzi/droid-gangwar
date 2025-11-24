package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import com.example.droidgangwar.databinding.FragmentAlleywayBinding
import com.example.droidgangwar.data.AlleywayRoomData
import com.example.droidgangwar.data.RandomEventData
import com.example.droidgangwar.model.AlleywayRoom
import com.example.droidgangwar.model.RandomEvent
import com.example.droidgangwar.model.AlleywayGameState
import com.example.droidgangwar.model.GameState
import com.example.droidgangwar.model.EventType

class AlleywayFragment : Fragment() {

    private var _binding: FragmentAlleywayBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()
    
    private var alleywayGameState = AlleywayGameState()
    private var currentRoom: AlleywayRoom? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlleywayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize current room
        currentRoom = AlleywayRoomData.getRoomById(alleywayGameState.currentRoom)
        alleywayGameState.visitedRooms.add(alleywayGameState.currentRoom)

        // Observe game state
        gameViewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            updateUI(gameState)
        }

        setupAlleywayActions()
        updateRoomUI()
    }

    private fun updateUI(gameState: GameState) {
        binding.apply {
            moneyText.text = "$${gameState.money}"
            healthText.text = "${gameState.health}/${gameState.maxHealth}"
        }
    }

    private fun setupAlleywayActions() {
        binding.apply {
            searchButton.setOnClickListener {
                searchAlley()
            }

            exploreButton.setOnClickListener {
                exploreRoom()
            }

            moveButton.setOnClickListener {
                showMoveOptions()
            }

            backButton.setOnClickListener {
                gameViewModel.navigateToScreen("city")
            }

            lookButton.setOnClickListener {
                lookAround()
            }
        }
    }
    
    private fun updateRoomUI() {
        currentRoom?.let { room ->
            binding.apply {
                // Update room description
                roomDescriptionText.text = room.description
                
                // Show search option if room is searchable
                if (room.searchable) {
                    searchButton.visibility = View.VISIBLE
                    searchButton.text = "Search ${room.title}"
                } else {
                    searchButton.visibility = View.GONE
                }
                
                // Show exploration option
                exploreButton.visibility = View.VISIBLE
                exploreButton.text = "Explore ${room.title}"
                
                // Show available exits
                val exitsText = room.exits.entries.joinToString(", ") { (direction, targetRoom) ->
                    "${direction.replace("_", " ")} (${AlleywayRoomData.getRoomById(targetRoom)?.title ?: "Unknown"})"
                }
                exitsTextView.text = "Exits: $exitsText"
                
                // Show room atmosphere
                atmosphereTextView.text = "Atmosphere: ${room.atmosphere.replace("_", " ").replaceFirstChar { it.uppercase() }}"
            }
        }
    }

    private fun searchAlley() {
        val gameState = gameViewModel.gameState.value ?: return
        currentRoom?.let { room ->
            if (room.searchable) {
                // Generate search event
                val searchEvent = RandomEventData.generateAlleywaySearchEvent(room.roomId, room.searchableItems)
                
                // Apply event effects
                RandomEventData.applyEventEffects(searchEvent, gameState)
                
                // Show results
                showEventDialog(searchEvent.title, searchEvent.description)
                
                // Update game state
                gameViewModel.updateGameState(gameState)
                gameViewModel.incrementSteps()
                
                // Increment room search count
                val currentCount = alleywayGameState.roomSearchCount[room.roomId] ?: 0
                alleywayGameState.roomSearchCount.put(room.roomId, currentCount + 1)
            }
        }
    }
    
    private fun exploreRoom() {
        val gameState = gameViewModel.gameState.value ?: return
        
        // Generate random exploration event
        val exploreEvent = RandomEventData.generateRandomEvent(gameState)
        
        // Check if event meets requirements
        if (RandomEventData.hasMeetRequirements(exploreEvent, gameState)) {
            // Apply event effects
            RandomEventData.applyEventEffects(exploreEvent, gameState)
            
            // Show results
            showEventDialog(exploreEvent.title, exploreEvent.description)
            
            // Handle combat events
            when (exploreEvent.type) {
                EventType.POLICE_CHASE,
                EventType.GANG_FIGHT,
                EventType.SQUIDIE_HIT_SQUAD -> {
                    // Start combat - for now just show message
                    showMessage("Combat initiated! This would normally start a MUD fight sequence.")
                }
                else -> {
                    // Regular event - continue exploring
                }
            }
            
            // Update game state
            gameViewModel.updateGameState(gameState)
            gameViewModel.incrementSteps()
        }
    }
    
    private fun lookAround() {
        currentRoom?.let { room ->
            val lookText = buildString {
                append("${room.title}\n\n")
                append(room.description)
                
                if (room.searchable) {
                    append("\n\nThis area looks searchable. You might find hidden items or dangers.")
                }
                
                val atmoshereDescription = when (room.atmosphere) {
                    "foreboding" -> "An ominous feeling fills the air. Danger could strike at any moment."
                    "mysterious" -> "Strange sounds and shadows suggest secrets are hidden here."
                    "violent" -> "Blood stains and weapons suggest this place has seen many fights."
                    "peaceful" -> "Despite the urban decay, this spot feels relatively safe."
                    else -> "The atmosphere is tense but manageable."
                }
                append("\n\n$atmoshereDescription")
            }
            
            showMessage(lookText)
        }
    }
    
    private fun showMoveOptions() {
        currentRoom?.let { room ->
            val moves = room.exits.entries.toList()
            
            if (moves.isEmpty()) {
                showMessage("No exits available from this room.")
                return
            }
            
            val options = moves.map { (direction, targetRoom) ->
                val roomTitle = AlleywayRoomData.getRoomById(targetRoom)?.title ?: "Unknown Location"
                "${direction.replace("_", " ").replaceFirstChar { it.uppercase() }} - $roomTitle"
            }.toTypedArray()
            
            // Show dialog for direction selection
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Choose Direction to Move")
                .setItems(options) { _, which ->
                    val selectedMove = moves[which]
                    moveToRoom(selectedMove.value)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
    
    private fun moveToRoom(roomId: String) {
        if (roomId == "city") {
            // Special case - return to city
            gameViewModel.navigateToScreen("city")
            return
        }
        
        val newRoom = AlleywayRoomData.getRoomById(roomId)
        if (newRoom != null) {
            alleywayGameState.currentRoom = roomId
            alleywayGameState.visitedRooms.add(roomId)
            currentRoom = newRoom
            
            // Update UI
            updateRoomUI()
            
            // Advance step counter
            gameViewModel.incrementSteps()
            
            // Show room entry message
            showMessage("You move to: ${newRoom.title}\n\n${newRoom.description}")
        } else {
            showMessage("Cannot move to that location.")
        }
    }

    private fun searchAlleyLegacy() {
        val gameState = gameViewModel.gameState.value ?: return
        
        val searchResults = listOf(
            "You find $200 hidden behind a dumpster",
            "You discover a stash of weed worth $500",
            "You find nothing of value",
            "You find some ammo - 10 rounds",
            "You find a knife - useful for close combat"
        )
        
        val result = searchResults.random()
        showMessage("Alleyway Search:\n\n$result")
    }

    private fun fightInAlley() {
        val gameState = gameViewModel.gameState.value ?: return
        
        showMessage("alleyway Fight:\n\nYou engage in a violent encounter in the dark alleyway!")
    }

    private fun hideInAlley() {
        showMessage("Alleyway:\n\nYou hide in the shadows and observe the street. Nothing interesting happens.")
    }

    private fun showMessage(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showEventDialog(title: String, description: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
