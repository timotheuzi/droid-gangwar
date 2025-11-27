package com.example.droidgangwar.model

import kotlin.random.Random

object CombatSystem {

    // Gory and Ironic Battle descriptions
    private val pistolMessages = listOf(
        "You squeeze the trigger and blow a hole through the %s's chest! It's a bloody mess!",
        "Pop! Pop! The %s drops like a sack of potatoes, brains leaking onto the pavement.",
        "You channel your inner action hero and blast the %s away. Messy cleanup ahead.",
        "The %s didn't see it coming. Your bullet rearranged their face permanently.",
        "You cap the %s. Blood sprays onto the graffiti-covered wall.",
        "One shot, one kill. The %s collapses in a heap of twitching limbs.",
        "You fire. The %s looks surprised as their chest explodes outward.",
        "Blam! The %s falls back, clutching a wound that won't stop gushing."
    )

    private val ghostGunMessages = listOf(
        "Your ghost gun whispers death, silencing the %s forever.",
        "Untraceable and deadly. The %s collapses, never knowing what hit them.",
        "Plastic fantastic! The %s is shredded by your phantom weaponry.",
        "No serial number, no witnesses. The %s drops dead.",
        "The 3D printed parts hold together just long enough to end the %s.",
        "Silenced death. The %s slumps over before hearing the shot."
    )

    private val uziMessages = listOf(
        "BRRRRT! You spray the area, turning the %s into swiss cheese.",
        "Controlled chaos! The %s dances the lead tango before falling still.",
        "Spray and pray works! The %s is riddled with holes.",
        "The street lights up with muzzle flash. The %s is cut in half.",
        "You empty the clip. The %s is unrecognizable meat.",
        "Lead storm! The %s jerks violently as bullets tear through them."
    )

    private val grenadeMessages = listOf(
        "KA-BOOM! Chunks of %s rain down around you. Gross, but effective.",
        "Fire in the hole! The explosion turns the %s into red mist.",
        "You toss a pineapple and the %s catches it. Bad move for them.",
        "Explosion! The shockwave liquefies the %s's insides.",
        "Frag out! Shrapnel tears the %s apart like wet tissue paper.",
        "The blast throws the %s into the air. They land in pieces."
    )

    private val missileMessages = listOf(
        "Overkill much? The missile vaporizes the %s instantly.",
        "Rocket man! The %s is reduced to a smoking crater.",
        "Boom goes the dynamite! There's nothing left of the %s to bury.",
        "You send a rocket downrange. The %s becomes a fine pink mist.",
        "Direct hit! The %s is erased from existence.",
        "Thermal detonation. The %s is cooked to perfection."
    )

    private val batMessages = listOf(
        "CRACK! You home run the %s's head into the bleachers.",
        "The barbed wire catches and tears. The %s screams as you swing again.",
        "Batter up! You beat the %s into a bloody pulp.",
        "You swing for the fences. The %s's skull caves in with a wet crunch.",
        "Thwack! Barbs rip flesh from bone. The %s gurgles and falls.",
        "You channel your rage into the swing. The %s is now a stain on the pavement."
    )
    
    private val knucklesMessages = listOf(
        "Crack! You land a solid right hook to the %s's jaw.",
        "The brass knuckles connect with a sickening crunch against the %s's ribs.",
        "You pummel the %s with a flurry of brass-weighted blows.",
        "The %s spits teeth as your fist meets their face.",
        "Wham! The %s doubles over as you punch them in the gut.",
        "You introduce your brass friends to the %s's nose."
    )

    private val knifeMessages = listOf(
        "Slice and dice! You carve your initials into the %s.",
        "Up close and personal. The %s gurgles as you slide the blade in.",
        "Stab! Stab! Stab! It's a frenzy of red as the %s falls.",
        "You open the %s up from belly to throat. Guts spill out.",
        "A quick slash across the throat. The %s chokes on their own blood.",
        "You stick the pointy end in. The %s looks shocked, then dead."
    )

    private val explodingBulletMessages = listOf(
        "The exploding round detonates inside the %s! It's raining guts!",
        "Blam! The %s expands rapidly then bursts. Messy.",
        "Explosive tipped justice! The %s is blown apart from the inside.",
        "Pop goes the weasel! The %s scatters across the sidewalk.",
        "The bullet enters small and leaves big. Half the %s is missing.",
        "Internal combustion! The %s's torso becomes an open concept."
    )
    
    private val gangAttackMessages = listOf(
        "Your gang swarms the %s! They beat them senseless.",
        "Your crew opens fire! The %s is pinned down and riddled with bullets.",
        "Gang violence at its finest! Your members tear the %s apart.",
        "Your homies jump in and stomp the %s into the ground.",
        "The %s is overwhelmed by your gang's numbers. It's a massacre."
    )

    private val defeatMessages = listOf(
        "You fought bravely, but the street always wins. You are dead.",
        "Lights out. The last thing you see is the pavement rushing up to meet you.",
        "Wasted. Your empire crumbles before it even began.",
        "The darkness takes you. Another body for the morgue.",
        "You gasp your last breath in a dirty alley. Game over."
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
        
        // --- TURN 1: PLAYER ATTACK ---
        val playerDamage = calculatePlayerDamage(gameState, weapon, enemyType, result.fightLog)
        
        // --- TURN 2: GANG MEMBERS ATTACK (if any) ---
        val gangDamage = calculateGangDamage(gameState, enemyType, result.fightLog)
        
        val totalDamage = playerDamage + gangDamage
        
        // Apply damage to enemies
        // Assuming simple HP pool model for group combat or per-unit
        // Let's stick to the model where total damage reduces the count based on approx HP
        val damagePerEnemy = if (currentEnemyCount > 0) 15.0 else 1.0 // Approx HP per enemy
        val kills = (totalDamage / damagePerEnemy).toInt()
        val actualKills = minOf(kills, currentEnemyCount)
        
        currentEnemyCount -= actualKills
        result.enemiesKilled = actualKills
        result.damageDealt = totalDamage.toInt()
        
        if (actualKills > 0) {
             result.fightLog.add("You and your gang killed $actualKills $enemyType!")
        } else if (totalDamage > 0) {
            result.fightLog.add("You wounded the $enemyType!")
        }

        // --- TURN 3: ENEMY COUNTER ATTACK ---
        if (currentEnemyCount > 0) {
            val enemyDamage = calculateEnemyDamage(currentEnemyCount, gameState, result.fightLog)
            gameState.takeDamage(enemyDamage)
            result.damageTaken = enemyDamage
            result.fightLog.add("The remaining $currentEnemyCount $enemyType attack you for $enemyDamage damage!")
            
            if (gameState.health <= 0) {
                result.defeat = true
                result.fightLog.add(defeatMessages.random())
            }
        } else {
            result.victory = true
            result.fightLog.add("You have defeated all enemies!")
        }

        return result
    }

    private fun calculatePlayerDamage(gameState: GameState, weapon: String, enemyType: String, fightLog: MutableList<String>): Double {
        val shouldUseExploding = gameState.weapons.useExplodingBullets && gameState.weapons.explodingBullets > 0
        var damage = 0.0
        var message = ""

        // Check ammo and deduct
        when (weapon) {
            "pistol" -> {
                if (shouldUseExploding) {
                     if (gameState.weapons.explodingBullets > 0) {
                         gameState.weapons.explodingBullets--
                     } else {
                         fightLog.add("Click! You're out of exploding bullets!")
                         return 0.0
                     }
                } else {
                    if (gameState.weapons.bullets > 0) {
                        gameState.weapons.bullets--
                    } else {
                        fightLog.add("Click! You're out of bullets!")
                        return 0.0
                    }
                }
                
                damage = Random.nextInt(15, 30).toDouble()
                if (shouldUseExploding) {
                    damage *= 2.0
                    message = explodingBulletMessages.random().replace("%s", enemyType)
                } else {
                    message = pistolMessages.random().replace("%s", enemyType)
                }
                if (gameState.pistolUpgraded) {
                    damage += 10
                    message += " (Upgraded)"
                }
            }
            "ghost_gun" -> {
                if (gameState.weapons.bullets > 0) {
                    gameState.weapons.bullets--
                    damage = Random.nextInt(20, 35).toDouble()
                    message = ghostGunMessages.random().replace("%s", enemyType)
                } else {
                    fightLog.add("Click! You're out of bullets for your ghost gun!")
                    return 0.0
                }
            }
            "uzi" -> {
                 val ammoCost = 3
                 if (shouldUseExploding) {
                     if (gameState.weapons.explodingBullets >= ammoCost) {
                         gameState.weapons.explodingBullets -= ammoCost
                         damage = Random.nextInt(30, 50).toDouble() * 1.5
                         message = uziMessages.random().replace("%s", enemyType) + " WITH EXPLOSIVE ROUNDS!"
                     } else {
                          fightLog.add("Click! Not enough exploding ammo for Uzi!")
                          return 0.0
                     }
                } else {
                    if (gameState.weapons.bullets >= ammoCost) {
                        gameState.weapons.bullets -= ammoCost
                        damage = Random.nextInt(30, 50).toDouble()
                        message = uziMessages.random().replace("%s", enemyType)
                    } else {
                         fightLog.add("Click! Not enough ammo for Uzi!")
                         return 0.0
                    }
                }
            }
            "grenade" -> {
                if (gameState.weapons.grenades > 0) {
                    gameState.weapons.grenades--
                    damage = Random.nextInt(60, 100).toDouble()
                    message = grenadeMessages.random().replace("%s", enemyType)
                } else {
                    fightLog.add("You reach for a grenade but find only lint!")
                    return 0.0
                }
            }
            "missile_launcher" -> {
                 if (gameState.weapons.missiles > 0) {
                    gameState.weapons.missiles--
                    damage = Random.nextInt(100, 200).toDouble()
                    message = missileMessages.random().replace("%s", enemyType)
                } else {
                    fightLog.add("No missiles loaded!")
                    return 0.0
                }
            }
            "barbed_wire_bat" -> {
                damage = Random.nextInt(20, 40).toDouble()
                message = batMessages.random().replace("%s", enemyType)
            }
            "brass_knuckles" -> {
                damage = Random.nextInt(15, 25).toDouble()
                message = knucklesMessages.random().replace("%s", enemyType)
            }
            "knife" -> {
                damage = Random.nextInt(10, 20).toDouble()
                message = knifeMessages.random().replace("%s", enemyType)
            }
            else -> {
                damage = 1.0
                message = "You flail wildly at the $enemyType!"
            }
        }
        
        fightLog.add(message)
        return damage
    }
    
    private fun calculateGangDamage(gameState: GameState, enemyType: String, fightLog: MutableList<String>): Double {
        val memberCount = gameState.members
        if (memberCount <= 1) return 0.0 // Just the player
        
        // Each member contributes some damage based on generalized weaponry
        // Let's say each member has a 50% chance to hit for 5-15 damage
        var totalGangDamage = 0.0
        var hits = 0
        
        // Limit calculation loop for performance if gang is huge, but cap at 50 members fighting effectively
        val fightingMembers = minOf(memberCount - 1, 50) 
        
        for (i in 1..fightingMembers) {
            if (Random.nextBoolean()) { // 50% hit chance
                totalGangDamage += Random.nextInt(5, 15).toDouble()
                hits++
            }
        }
        
        if (totalGangDamage > 0) {
            val message = gangAttackMessages.random().replace("%s", enemyType)
            fightLog.add("$message (Gang dealt ${totalGangDamage.toInt()} damage)")
        }
        
        return totalGangDamage
    }

    private fun calculateEnemyDamage(enemyCount: Int, gameState: GameState, fightLog: MutableList<String>): Int {
        // Base damage per enemy
        val baseDamagePerEnemy = 5
        var totalDamage = enemyCount * Random.nextInt(baseDamagePerEnemy - 2, baseDamagePerEnemy + 5)
        
        // Vest reduction
        if (gameState.weapons.vest > 0) {
            val reduction = minOf(totalDamage, gameState.weapons.vest)
            totalDamage -= (reduction * 0.5).toInt() // 50% effectiveness
            if (totalDamage < 0) totalDamage = 0
            fightLog.add("Your vest absorbed some of the damage.")
        }
        
        return totalDamage
    }
    
    fun useDrug(gameState: GameState, drug: String): DrugUseResult {
         return when (drug) {
            "crack" -> {
                if (gameState.drugs.crack > 0) {
                    gameState.drugs.crack--
                    gameState.takeDamage(5)
                    DrugUseResult(true, "You smoke some crack! You feel invincible but it hurts!", -5)
                } else {
                    DrugUseResult(false, "You're out of crack!", 0)
                }
            }
            "percs" -> {
                if (gameState.drugs.percs > 0) {
                    gameState.drugs.percs--
                    gameState.heal(15)
                    DrugUseResult(true, "You pop a perc. The pain fades away.", 15)
                } else {
                    DrugUseResult(false, "You're out of percs!", 0)
                }
            }
            else -> {
                DrugUseResult(false, "You can't use that in a fight!", 0)
            }
        }
    }

    fun fleeCombat(): Pair<Boolean, String> {
        val chance = Random.nextDouble()
        return if (chance > 0.5) {
            Pair(true, "You scramble away into the shadows, living to fight another day.")
        } else {
            Pair(false, "You try to run, but they cut off your escape!")
        }
    }
}
