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
        enemyHealth: Double
    ): CombatResult {
        val result = CombatResult()
        var currentEnemyHealth = enemyHealth
        var currentEnemyCount = enemyCount

        // Player's attack - these must be var because they might need reassignment
        var playerDamage = calculatePlayerDamage(gameState, weapon, result.fightLog)
        currentEnemyHealth = currentEnemyHealth - playerDamage

        // Gang members' attacks - these must be var because they might need reassignment
        var gangDamage = calculateGangDamage(gameState, currentEnemyCount, result.fightLog)
        currentEnemyHealth = currentEnemyHealth - gangDamage

        // Total damage - must be var because it might be modified later
        var totalDamage = playerDamage + gangDamage

        // Calculate enemies killed - must be var because it might be modified
        var enemiesKilled = calculateEnemiesKilled(totalDamage, currentEnemyCount, enemyHealth)
        currentEnemyCount = currentEnemyCount - enemiesKilled

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

        result.damageDealt = totalDamage.toInt()
        result.enemiesKilled = enemiesKilled
        return result
    }

    private fun calculatePlayerDamage(gameState: GameState, weapon: String, fightLog: MutableList<String>): Double {
        return when (weapon) {
            "pistol" -> {
                if (gameState.weapons.pistols > 0 && gameState.weapons.bullets > 0) {
                    val baseDamage = Random.nextInt(15, 26).toDouble()
                    val damage = when (gameState.pistolUpgradeType) {
                        "damage" -> baseDamage * 1.5 // +50% damage
                        "accuracy" -> baseDamage + 5.0 // +5 flat damage bonus
                        "magazine" -> baseDamage // No damage bonus but +50% ammo capacity
                        else -> baseDamage
                    }
                    val upgradeText = if (gameState.pistolUpgraded) " (upgraded)" else ""
                    fightLog.add("${attackMessages["pistol"]}${upgradeText} (${damage.toInt()} damage)")
                    damage
                } else 0.0
            }
            "ghost_gun" -> {
                if (gameState.weapons.ghostGuns > 0 && gameState.weapons.bullets > 0) {
                    val damage = Random.nextInt(15, 26).toDouble()
                    fightLog.add("${attackMessages["ghost_gun"]} (${damage.toInt()} damage)")
                    damage
                } else 0.0
            }
            "uzi" -> {
                if (gameState.weapons.uzis > 0 && gameState.weapons.bullets >= 3) {
                    val damage = Random.nextInt(20, 41).toDouble()
                    fightLog.add("${attackMessages["uzi"]} (${damage.toInt()} damage)")
                    damage
                } else 0.0
            }
            "grenade" -> {
                if (gameState.weapons.grenades > 0) {
                    val damage = Random.nextInt(30, 61).toDouble()
                    fightLog.add("${attackMessages["grenade"]} (${damage.toInt()} damage)")
                    damage
                } else 0.0
            }
            "missile_launcher" -> {
                if (gameState.weapons.missileLauncher > 0 && gameState.weapons.missiles > 0) {
                    val damage = Random.nextInt(50, 101).toDouble()
                    fightLog.add("${attackMessages["missile_launcher"]} (${damage.toInt()} damage)")
                    damage
                } else 0.0
            }
            "barbed_wire_bat" -> {
                if (gameState.weapons.barbedWireBat > 0) {
                    val damage = Random.nextInt(25, 46).toDouble()
                    fightLog.add("${attackMessages["barbed_wire_bat"]} (${damage.toInt()} damage)")
                    damage
                } else 0.0
            }
            "knife" -> {
                val damage = Random.nextInt(10, 21).toDouble()
                fightLog.add("${attackMessages["knife"]} (${damage.toInt()} damage)")
                damage
            }
            else -> 0.0
        }
    }

    private fun calculateGangDamage(gameState: GameState, enemyCount: Int, fightLog: MutableList<String>): Double {
        val gangMembers = gameState.members - 1
        if (gangMembers <= 0) return 0.0

        val damage = gangMembers.toDouble() * Random.nextInt(1, 6)
        fightLog.add("Your gang members deal ${damage.toInt()} damage!")
        return damage
    }

    private fun calculateEnemiesKilled(totalDamage: Double, enemyCount: Int, maxEnemyHealth: Double): Int {
        if (totalDamage <= 0) return 0

        val estimatedKilled = minOf(enemyCount, maxOf(1, (totalDamage / (maxEnemyHealth / enemyCount)).toInt()))

        // If damage exceeds remaining health, kill all remaining enemies
        val totalEnemyHp = enemyCount * maxEnemyHealth
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
