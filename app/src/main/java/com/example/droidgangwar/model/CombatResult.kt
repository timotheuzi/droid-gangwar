package com.example.droidgangwar.model

data class CombatResult(
    var victory: Boolean = false,
    var defeat: Boolean = false,
    var damageDealt: Int = 0,
    var damageTaken: Int = 0,
    var enemiesKilled: Int = 0,
    var message: String = "",
    var fightLog: MutableList<String> = mutableListOf()
)

data class DrugUseResult(
    val success: Boolean = false,
    val message: String = "",
    val healthChange: Int = 0
)
