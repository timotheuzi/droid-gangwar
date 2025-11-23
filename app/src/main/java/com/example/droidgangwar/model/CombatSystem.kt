package com.example.droidgangwar.model

import kotlin.random.Random

object CombatSystem {

    // Battle descriptions (similar to the JSON in Python version)
    private val attackMessages = mapOf(
        "pistol" to "You fire your pistol!",
        "ghost_gun" to "You fire your ghost gun!",
        "uzi" to "You fire your Uzi!",
        "grenade" to "You throw a grenade!",
        "missile_launcher" to "You fire a missile!",
        "barbed_wire_bat" to "You swing your barbed wire bat!",
        "knife" to "You stab with your knife!"
    )

    private val killMessages = mapOf(
        "police_singular" to "You killed a police officer!",
        "police_plural" to "You killed {count} police officers!",
        "gang_singular" to "You killed a gang member!",
        "gang_plural" to "You killed {count} gang members!",
        "squidie_singular" to "You killed a Squidie!",
        "squidie_plural" to "You killed {count} Squiddies!",
        "generic_singular" to "You killed an enemy!",
        "generic_plural" to "You killed {count} enemies!"
    )

    private val victoryMessages = mapOf(
        "complete_victory" to "Victory! You defeated all enemies!",
        "partial_victory" to "Victory! You defeated some enemies!"
    )

    private val defeatMessages = mapOf(
        "standard" to "Defeat! You were defeated!",
        "final_death" to "Final death! Game over!",
        "damage_taken" to "You took {damage} damage!"
    )

    private val combatStatus = mapOf(
        "use_drug_crack" to "You used crack and took damage!",
        "use_drug_percs" to "You used percs and healed!",
        "drug_generic" to "You used a drug!"
    )

    fun calculateCombat(
        gameState: GameState,
        weapon: String,
        enemyType: String,
        enemyCount: Int,
        enemyHealth: Int
    ): CombatResult {
        val result = CombatResult()
        var currentEnemyHealth = enemyHealth
        var currentEnemyCount = enemyCount

        // Player's attack - these must be var because they might need reassignment
        var playerDamage = calculatePlayerDamage(gameState, weapon, result.fightLog)
        currentEnemyHealth -= playerDamage

        // Gang members' attacks - these must be var because they might need reassignment
        var gangDamage = calculateGangDamage(gameState, currentEnemyCount, result.fightLog)
        currentEnemyHealth -= gangDamage

        // Total damage - must be var because it might be modified later
        var totalDamage = playerDamage + gangDamage

        // Calculate enemies killed - must be var because it might be modified
        var enemiesKilled = calculateEnemiesKilled(totalDamage, currentEnemyCount, enemyHealth)
        currentEnemyCount -= enemiesKilled

        // Add kill messages
        addKillMessages(enemyType, enemiesKilled, result.fightLog)

        // Enemy counterattack
        if (currentEnemyHealth > 0 && currentEnemyCount > 0) {
            val enemyDamage = calculateEnemyDamage(currentEnemyCount, gameState, result.fightLog)
            result.fightLog.add("The $enemyType counterattack, dealing $enemyDamage damage!")
            result.damageTaken = enemyDamage
        }

        // Check win/lose conditions
        if (currentEnemyHealth <= 0) {
            result.victory = true
            result.fightLog.add(victoryMessages["complete_victory"] ?: "Victory!")

            // Chance to recruit defeated enemy
            if (enemyType != "Police Officers" && Random.nextFloat() < 0.3f) {
                result.fightLog.add("One of your defeated enemies has joined your gang!")
            }
        }

        result.damageDealt = totalDamage
        result.enemiesKilled = enemiesKilled
        return result
    }

    private fun calculatePlayerDamage(gameState: GameState, weapon: String, fightLog: MutableList<String>): Int {
        return when (weapon) {
            "pistol" -> {
                if (gameState.weapons.pistols > 0 && gameState.weapons.bullets > 0) {
                    val damage = Random.nextInt(15, 26)
                    fightLog.add("${attackMessages["pistol"]} ($damage damage)")
                    damage
                } else 0
            }
            "ghost_gun" -> {
                if (gameState.weapons.ghostGuns > 0 && gameState.weapons.bullets > 0) {
                    val damage = Random.nextInt(15, 26)
                    fightLog.add("${attackMessages["ghost_gun"]} ($damage damage)")
                    damage
                } else 0
            }
            "uzi" -> {
                if (gameState.weapons.uzis > 0 && gameState.weapons.bullets >= 3) {
                    val damage = Random.nextInt(20, 41)
                    fightLog.add("${attackMessages["uzi"]} ($damage damage)")
                    damage
                } else 0
            }
            "grenade" -> {
                if (gameState.weapons.grenades > 0) {
                    val damage = Random.nextInt(30, 61)
                    fightLog.add("${attackMessages["grenade"]} ($damage damage)")
                    damage
                } else 0
            }
            "missile_launcher" -> {
                if (gameState.weapons.missileLauncher > 0 && gameState.weapons.missiles > 0) {
                    val damage = Random.nextInt(50, 101)
                    fightLog.add("${attackMessages["missile_launcher"]} ($damage damage)")
                    damage
                } else 0
            }
            "barbed_wire_bat" -> {
                if (gameState.weapons.barbedWireBat > 0) {
                    val damage = Random.nextInt(25, 46)
                    fightLog.add("${attackMessages["barbed_wire_bat"]} ($damage damage)")
                    damage
                } else 0
            }
            "knife" -> {
                val damage = Random.nextInt(10, 21)
                fightLog.add("${attackMessages["knife"]} ($damage damage)")
                damage
            }
            else -> 0
        }
    }

    private fun calculateGangDamage(gameState: GameState, enemyCount: Int, fightLog: MutableList<String>): Int {
        var totalGangDamage = 0

        if (gameState.members <= 1) return 0 // No gang members to help

        val gangMemberCount = minOf(gameState.members - 1, 5) // Max 5 gang members attack

        // Check available weapons for gang members
        val availableWeapons = mutableListOf<String>()
        if (gameState.weapons.pistols > 1 && gameState.weapons.bullets >= gangMemberCount) {
            availableWeapons.add("pistol")
        }
        if (gameState.weapons.uzis > 0 && gameState.weapons.bullets >= gangMemberCount * 3) {
            availableWeapons.add("uzi")
        }
        if (gameState.weapons.barbedWireBat > 0) {
            availableWeapons.add("barbed_wire_bat")
        }
        if (gameState.weapons.knife > 0) {
            availableWeapons.add("knife")
        }

        if (availableWeapons.isEmpty()) return 0

        // Each gang member attacks
        for (i in 0 until gangMemberCount) {
            val weapon = availableWeapons.random()
            val damage = when (weapon) {
                "pistol" -> {
                    Random.nextInt(10, 21)
                }
                "uzi" -> {
                    Random.nextInt(15, 31)
                }
                "barbed_wire_bat" -> Random.nextInt(15, 26)
                "knife" -> Random.nextInt(8, 16)
                else -> 0
            }

            if (damage > 0) {
                totalGangDamage += damage
                fightLog.add("Gang member ${i + 1} attacks with ${weapon.replace("_", " ")}, dealing $damage damage!")
            }
        }

        return totalGangDamage
    }

    private fun calculateEnemiesKilled(totalDamage: Int, enemyCount: Int, maxEnemyHealth: Int): Int {
        if (totalDamage <= 0) return 0

        // Estimate based on average enemy HP (around 20)
        val avgEnemyHp = 20
        val estimatedKilled = minOf(enemyCount, maxOf(1, totalDamage / avgEnemyHp))

        // If damage exceeds remaining health, kill all remaining enemies
        val totalEnemyHp = enemyCount * (maxEnemyHealth / maxOf(1, enemyCount))
        return if (totalDamage >= totalEnemyHp) enemyCount else estimatedKilled
    }

    private fun addKillMessages(enemyType: String, enemiesKilled: Int, fightLog: MutableList<String>) {
        if (enemiesKilled <= 0) return

        val messageKey = when {
            enemyType.contains("Police") -> {
                if (enemiesKilled == 1) "police_singular" else "police_plural"
            }
            enemyType.contains("Squidie") -> {
                if (enemiesKilled == 1) "squidie_singular" else "squidie_plural"
            }
            enemyType.contains("Gang") -> {
                if (enemiesKilled == 1) "gang_singular" else "gang_plural"
            }
            else -> {
                if (enemiesKilled == 1) "generic_singular" else "generic_plural"
            }
        }

        val message = killMessages[messageKey] ?: "You killed enemies!"
        fightLog.add(if (message.contains("{count}")) message.replace("{count}", enemiesKilled.toString()) else message)
    }

    private fun calculateEnemyDamage(enemyCount: Int, gameState: GameState, fightLog: MutableList<String>): Int {
        val baseDamage = Random.nextInt(5, 16) * enemyCount

        // Check if vest protects
        return if (gameState.weapons.vest > 0 && Random.nextFloat() < 0.5f) {
            val reducedDamage = maxOf(0, baseDamage - 20)
            fightLog.add("Your vest absorbs some damage! You take $reducedDamage damage instead of $baseDamage!")
            reducedDamage
        } else {
            baseDamage
        }
    }

    fun useDrug(gameState: GameState, drug: String): DrugUseResult {
        return when (drug) {
            "crack" -> {
                if (gameState.drugs.crack > 0) {
                    DrugUseResult(true, combatStatus["use_drug_crack"] ?: "You used crack!", -5)
                } else {
                    DrugUseResult(false, "You have no crack to use!", 0)
                }
            }
            "percs" -> {
                if (gameState.drugs.percs > 0) {
                    DrugUseResult(true, combatStatus["use_drug_percs"] ?: "You used percs!", 10)
                } else {
                    DrugUseResult(false, "You have no percs to use!", 0)
                }
            }
            else -> {
                DrugUseResult(false, combatStatus["drug_generic"] ?: "You used a drug!", 0)
            }
        }
    }

    fun fleeCombat(): Pair<Boolean, String> {
        val success = Random.nextFloat() < 0.4f // 40% chance
        val message = if (success) {
            "You successfully flee from combat!"
        } else {
            "You try to flee but your enemies block your escape!"
        }
        return Pair(success, message)
    }
}
