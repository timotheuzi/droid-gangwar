package com.example.droidgangwar.model

import com.google.gson.annotations.SerializedName
import kotlin.random.Random

data class GameState(
    @SerializedName("player_name")
    var playerName: String = "",

    @SerializedName("gang_name")
    var gangName: String = "",

    var money: Int = 1000,
    var account: Int = 0, // Bank account
    var loan: Int = 0,

    var members: Int = 1, // Gang members
    var squidies: Int = 25, // Enemy gang members

    var day: Int = 1,
    var health: Int = 30,
    var maxHealth: Int = 100,

    var steps: Int = 0,
    var maxSteps: Int = 15,

    var lives: Int = 3,
    var damage: Int = 0,

    var currentScore: Int = 0,
    var currentLocation: String = "city",

    var drugPrices: MutableMap<String, Int> = mutableMapOf(
        "weed" to 500,
        "crack" to 1000,
        "coke" to 2000,
        "ice" to 1500,
        "percs" to 800,
        "pixie_dust" to 3000
    ),

    var flags: Flags = Flags(),
    var weapons: Weapons = Weapons(),
    var drugs: Drugs = Drugs(),

    // Pistol upgrade properties
    var pistolUpgradeType: String = "none",
    var pistolUpgraded: Boolean = false,
    var pistolUpgradeToggle: Boolean = false
) {
    fun updateCurrentScore() {
        val moneyEarned = money + account
        val survivalScore = day * 100
        val moneyScore = moneyEarned / 1000
        currentScore = moneyScore + survivalScore
    }

    fun isGameOver(): Boolean = lives <= 0 || health <= 0

    fun canAfford(amount: Int): Boolean = money >= amount

    fun spendMoney(amount: Int): Boolean {
        return if (canAfford(amount)) {
            money -= amount
            true
        } else {
            false
        }
    }

    fun heal(amount: Int) {
        health = minOf(maxHealth, health + amount)
    }

    fun takeDamage(amount: Int) {
        // In classic gangwar, HP is life.
        health -= amount
        if (health <= 0) {
            // Handle death logic here or in the game loop
            // For now, ensure health reflects the damage
        }
    }

    fun advanceDay() {
        day++
        steps = 0
        updateDrugPrices()
    }

    private fun updateDrugPrices() {
        // Randomly fluctuate drug prices each day
        drugPrices.keys.forEach { drug ->
            val basePrice = when(drug) {
                "weed" -> 500
                "crack" -> 1000
                "coke" -> 2000
                "ice" -> 1500
                "percs" -> 800
                "pixie_dust" -> 3000
                else -> 500
            }
            // Volatility based on day
            val volatility = 0.1 + (day * 0.01) // Prices get more volatile as days pass
            val variation = (Random.nextDouble() * (volatility * 2)) - volatility
            drugPrices[drug] = (basePrice * (1 + variation)).toInt().coerceAtLeast(10)
        }
    }
}

data class Flags(
    @SerializedName("has_id")
    var hasId: Boolean = false,

    @SerializedName("has_info")
    var hasInfo: Boolean = false,

    @SerializedName("has_switch")
    var hasSwitch: Boolean = false
)

data class Weapons(
    var pistols: Int = 1,
    var bullets: Int = 10,
    var explodingBullets: Int = 0, // Special ammo
    var useExplodingBullets: Boolean = false, // User preference
    var uzis: Int = 0,
    var grenades: Int = 0,

    @SerializedName("barbed_wire_bat")
    var barbedWireBat: Int = 0,
    
    @SerializedName("brass_knuckles")
    var brassKnuckles: Int = 0,

    @SerializedName("missile_launcher")
    var missileLauncher: Int = 0,

    var missiles: Int = 0,
    var vest: Int = 0, // Defense points from vests
    var knife: Int = 0,

    @SerializedName("ghost_guns")
    var ghostGuns: Int = 0
) {
    fun canFightWithPistol(): Boolean = pistols > 0 && (bullets > 0 || explodingBullets > 0)
    fun canFightWithUzi(): Boolean = uzis > 0 && (bullets >= 3 || explodingBullets >= 3)
    fun canFightWithGrenade(): Boolean = grenades > 0
    fun canFightWithMissile(): Boolean = missileLauncher > 0 && missiles > 0
}

data class Drugs(
    var weed: Int = 0,
    var crack: Int = 5, // Start with 5 kilos of crack
    var coke: Int = 0,
    var ice: Int = 0,
    var percs: Int = 0,

    @SerializedName("pixie_dust")
    var pixieDust: Int = 0
) {
    fun getTotalValue(prices: Map<String, Int>): Int {
        return weed * (prices["weed"] ?: 0) +
                crack * (prices["crack"] ?: 0) +
                coke * (prices["coke"] ?: 0) +
                ice * (prices["ice"] ?: 0) +
                percs * (prices["percs"] ?: 0) +
                pixieDust * (prices["pixie_dust"] ?: 0)
    }
}
