package com.example.droidgangwar.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.droidgangwar.R
import com.example.droidgangwar.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_city, R.id.nav_crackhouse, R.id.nav_gunshack,
                R.id.nav_bank, R.id.nav_bar, R.id.nav_infobooth,
                R.id.nav_alleyway, R.id.nav_picknsave, R.id.nav_stats, R.id.nav_final_battle,
                R.id.nav_mud_fight
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Handle back button press with proper deprecation handling
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        // Observe game state changes to update UI
        gameViewModel.gameState.observe(this) { gameState ->
            updateUI(gameState)
        }

        gameViewModel.currentScreen.observe(this) { screen ->
            navigateToScreen(screen)
        }

        gameViewModel.gameMessage.observe(this) { message ->
            showMessage(message)
        }
    }

    private fun updateUI(gameState: com.example.droidgangwar.model.GameState) {
        // Update status bar to show location and player name
        supportActionBar?.title = gameState.currentLocation.replace("_", " ").capitalize()
        supportActionBar?.subtitle = gameState.playerName
    }

    private fun navigateToScreen(screen: String) {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        val destinationId = when (screen) {
            "city" -> R.id.nav_city
            "crackhouse" -> R.id.nav_crackhouse
            "gunshack" -> R.id.nav_gunshack
            "bank" -> R.id.nav_bank
            "bar" -> R.id.nav_bar
            "infobooth" -> R.id.nav_infobooth
            "alleyway" -> R.id.nav_alleyway
            "picknsave" -> R.id.nav_picknsave
            "stats" -> R.id.nav_stats
            "final_battle" -> R.id.nav_final_battle
            "game_over" -> R.id.nav_game_over
            "mud_fight" -> R.id.nav_mud_fight
            else -> R.id.nav_city
        }

        try {
            navController.navigate(destinationId)
        } catch (e: Exception) {
            // If navigation fails, show message
            showMessage("Could not navigate to $screen")
        }
    }

    private fun showMessage(message: String) {
        // Show toast or snackbar with game message
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
