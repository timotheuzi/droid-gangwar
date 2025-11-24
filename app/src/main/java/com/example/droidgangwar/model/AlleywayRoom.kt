package com.example.droidgangwar.model

import com.google.gson.annotations.SerializedName

data class AlleywayRoom(
    @SerializedName("room_id")
    val roomId: String,
    
    val title: String,
    val description: String,
    val exits: Map<String, String>, // Direction to room ID
    val searchable: Boolean = false,
    val searchableItems: List<String> = emptyList(),
    val traps: List<String> = emptyList(),
    val atmosphere: String = "normal"
)

data class AlleywayGameState(
    var currentRoom: String = "entrance",
    var visitedRooms: MutableSet<String> = mutableSetOf(),
    var roomSearchCount: MutableMap<String, Int> = mutableMapOf()
)

data class RandomEvent(
    val id: String,
    val title: String,
    val description: String,
    val type: EventType,
    val effects: Map<String, Int> = emptyMap(),
    val requirements: Map<String, Any> = emptyMap()
)

enum class EventType {
    BABY_MOMMA_INCIDENT,
    POLICE_CHASE,
    GANG_FIGHT,
    SQUIDIE_HIT_SQUAD,
    NPC_ENCOUNTER,
    TREASURE_FIND,
    HEALTH_REST,
    MONEY_FIND,
    DRUG_FIND,
    AMMO_FIND,
    WEAPON_FIND

}
