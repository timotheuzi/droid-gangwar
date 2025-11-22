package com.example.droidgangwar.model

data class CombatResult(
    val victory: Boolean = false,
    val defeat: Boolean = false,
    val damageDealt: Int = 0,
    val damageTaken: Int = 0,
    val enemiesKilled: Int = 0,
    val message: String = "",
    val fightLog: MutableList<String> = mutableListOf()
)

data class DrugUseResult(
    val success: Boolean = false,
    val message: String = "",
    val healthChange: Int = 0
)
