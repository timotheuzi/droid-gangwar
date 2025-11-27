package com.example.droidgangwar.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class HighScore(
    val name: String,
    val score: Int
)

class HighScoreRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("gangwar_highscores", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getHighScores(): List<HighScore> {
        val json = sharedPreferences.getString("scores", null)
        return if (json != null) {
            val type = object : TypeToken<List<HighScore>>() {}.type
            gson.fromJson(json, type)
        } else {
            getDefaultHighScores()
        }
    }

    fun addHighScore(name: String, score: Int) {
        val currentScores = getHighScores().toMutableList()
        currentScores.add(HighScore(name, score))
        currentScores.sortByDescending { it.score }
        
        // Keep only top 10
        val topScores = currentScores.take(10)
        
        val json = gson.toJson(topScores)
        sharedPreferences.edit().putString("scores", json).apply()
    }

    private fun getDefaultHighScores(): List<HighScore> {
        return listOf(
            HighScore("Big Tony", 1000000),
            HighScore("Ice Pick", 800000),
            HighScore("Salty Sam", 600000),
            HighScore("Lil' Pookie", 500000),
            HighScore("Crazy Steve", 400000),
            HighScore("The Rat", 300000),
            HighScore("Johnny Two-Toes", 200000),
            HighScore("Sneaky Pete", 100000),
            HighScore("Baby Face", 50000),
            HighScore("Unknown Punk", 10000)
        )
    }
}
