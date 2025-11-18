package com.example.droidgangwar.model

import kotlin.random.Random

data class CombatResult(
    val playerDamage: Int = 0,
    val enemyDamage: Int = 0,
    val playerKilled: Int = 0,
    val enemyKilled: Int = 0,
    val messages: List<String> = emptyList(),
    val victory: Boolean = false,
    val defeat: Boolean = false
)

class CombatSystem {

    companion object {
        private const val POLICE_DAMAGE_REDUCTION = 20

        fun calculateCombat(
            gameState: GameState,
            weapon: String,
            enemyType: String,
            enemyCount: Int,
            enemyHealth: Int
        ): CombatResult {
            val messages = mutableListOf<String>()
            var playerDamage = 0
            var enemyDamage = 0
            var playerKilled = 0
            var enemyKilled = 0

            // Player attack
            when (weapon) {
                "pistol" -> {
                    if (gameState.weapons.canFightWithPistol()) {
                        gameState.weapons.bullets--
                        val damage = Random.nextInt(15, 26)
                        playerDamage = damage
                        messages.add("You fire your pistol, dealing $damage damage!")
                    } else {
                        messages.add("You don't have enough bullets!")
                        return CombatResult(messages = messages)
                    }
                }
                "uzi" -> {
                    if (gameState.weapons.canFightWithUzi()) {
                        gameState.weapons.bullets -= 3
                        val damage = Random.nextInt(20, 41)
                        playerDamage = damage
                        messages.add("Your Uzi rattles, spraying $damage damage!")
                    } else {
                        messages.add("You don't have enough bullets for your Uzi!")
                        return CombatResult(messages = messages)
                    }
                }
                "grenade" -> {
                    if (gameState.weapons.canFightWithGrenade()) {
                        gameState.weapons.grenades--
                        val damage = Random.nextInt(30, 61)
                        playerDamage = damage
                        messages.add("Grenade explodes for $damage damage!")
                    } else {
                        messages.add("You don't have any grenades!")
                        return CombatResult(messages = messages)
                    }
                }
                "missile_launcher" -> {
                    if (gameState.weapons.canFightWithMissile()) {
                        gameState.weapons.missiles--
                        val damage = Random.nextInt(50, 101)
                        playerDamage = damage
                        messages.add("Missile launcher unleashes $damage damage!")
                    } else {
                        messages.add("You don't have missiles!")
                        return CombatResult(messages = messages)
                    }
                }
                "barbed_wire_bat" -> {
                    if (gameState.weapons.barbedWireBat > 0) {
                        val damage = Random.nextInt(25, 46)
                        playerDamage = damage
                        messages.add("Barbed wire bat tears for $damage damage!")
                    } else {
                        messages.add("You don't have a barbed wire bat!")
                        return CombatResult(messages = messages)
                    }
                }
                "knife" -> {
                    val damage = Random.nextInt(10, 21)
                    playerDamage = damage
                    messages.add("Knife strikes for $damage damage!")
                }
                else -> {
                    messages.add("Invalid weapon!")
                    return CombatResult(messages = messages)
                }
            }

            // Gang member attacks
            if (gameState.members > 1) {
                val gangMemberCount = minOf(gameState.members - 1, 5)
                for (i in 0 until gangMemberCount) {
                    val gangDamage = Random.nextInt(10, 21)
                    playerDamage += gangDamage
                    messages.add("Gang member ${i + 1} attacks for $gangDamage damage!")
                }
            }

            // Calculate enemies killed
            val avgEnemyHp = when (enemyType) {
                "Police Officers" -> 10
                "Squidie Hit Squad" -> 25
                else -> 15 // Regular gang members
            }

            enemyKilled = minOf(enemyCount, playerDamage / avgEnemyHp)

            // Enemy counterattack
            val baseEnemyDamage = Random.nextInt(5, 16) * enemyCount

            // Apply vest protection
            enemyDamage = if (gameState.weapons.vest > 0 && Random.nextFloat() < 0.5f) {
                gameState.weapons.vest--
                val reducedDamage = maxOf(0, baseEnemyDamage - POLICE_DAMAGE_REDUCTION)
                messages.add("Your vest absorbs damage! You take $reducedDamage instead of $baseEnemyDamage!")
                reducedDamage
            } else {
                messages.add("$enemyType counterattack for $baseEnemyDamage damage!")
                baseEnemyDamage
            }

            // Apply damage to player
            gameState.takeDamage(enemyDamage)

            // Check victory/defeat conditions
            val victory = enemyHealth - playerDamage <= 0
            val defeat = gameState.isGameOver()

            if (victory) {
                messages.add("VICTORY! You have defeated the $enemyType!")
                // Reduce enemy gang size
                gameState.squidies = maxOf(0, gameState.squidies - enemyKilled)
            } else if (defeat) {
                messages.add("DEFEAT! You have been defeated!")
            }

            return CombatResult(
                playerDamage = playerDamage,
                enemyDamage = enemyDamage,
                playerKilled = playerKilled,
                enemyKilled = enemyKilled,
                messages = messages,
                victory = victory,
                defeat = defeat
            )
        }

        fun useDrug(gameState: GameState, drug: String): String {
            return when (drug) {
                "crack" -> {
                    if (gameState.drugs.crack > 0) {
                        gameState.drugs.crack--
                        gameState.takeDamage(5)
                        "You smoke crack, gaining unholy strength but taking 5 damage!"
                    } else {
                        "You don't have any crack!"
                    }
                }
                "percs" -> {
                    if (gameState.drugs.percs > 0) {
                        gameState.drugs.percs--
                        val healed = minOf(10, gameState.damage)
                        gameState.damage = maxOf(0, gameState.damage - 10)
                        "You take Percocet, healing $healed damage!"
                    } else {
                        "You don't have any Percocet!"
                    }
                }
                "weed" -> {
                    if (gameState.drugs.weed > 0) {
                        gameState.drugs.weed--
                        "You smoke weed, heightening your senses!"
                    } else {
                        "You don't have any weed!"
                    }
                }
                "coke" -> {
                    if (gameState.drugs.coke > 0) {
                        gameState.drugs.coke--
                        "You snort cocaine, sharpening your reflexes!"
                    } else {
                        "You don't have any cocaine!"
                    }
                }
                "ice" -> {
                    if (gameState.drugs.ice > 0) {
                        gameState.drugs.ice--
                        gameState.takeDamage(5)
                        "You smoke ice, gaining immense power but taking 5 damage!"
                    } else {
                        "You don't have any ice!"
                    }
                }
                "pixie_dust" -> {
                    if (gameState.drugs.pixieDust > 0) {
                        gameState.drugs.pixieDust--
                        "You consume pixie dust, reality shifts around you!"
                    } else {
                        "You don't have any pixie dust!"
                    }
                }
                else -> "Unknown drug!"
            }
        }

        fun fleeCombat(): Pair<Boolean, String> {
            val success = Random.nextFloat() < 0.4f // 40% chance to flee
            val message = if (success) {
                "You successfully flee from combat!"
            } else {
                "You try to flee but your enemies attack!"
            }
            return Pair(success, message)
        }
    }
}
