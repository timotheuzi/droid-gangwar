package com.example.droidgangwar.data

import android.content.Context
import androidx.room.*
import com.example.droidgangwar.model.GameState
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey
    val id: Int = 1,
    val gameStateJson: String
)

@Dao
interface GameStateDao {
    @Query("SELECT * FROM game_state WHERE id = 1")
    suspend fun getGameState(): GameStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameState(gameState: GameStateEntity)

    @Query("DELETE FROM game_state WHERE id = 1")
    suspend fun deleteGameState()
}

@Database(entities = [GameStateEntity::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameStateDao(): GameStateDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "gangwar_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class GameRepository(private val context: Context) {
    private val database = GameDatabase.getDatabase(context)
    private val dao = database.gameStateDao()
    private val gson = Gson()

    suspend fun saveGameState(gameState: GameState) {
        val json = gson.toJson(gameState)
        val entity = GameStateEntity(gameStateJson = json)
        dao.saveGameState(entity)
    }

    suspend fun loadGameState(): GameState? {
        val entity = dao.getGameState()
        return entity?.let {
            try {
                gson.fromJson(it.gameStateJson, GameState::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun hasSavedGame(): Boolean {
        return dao.getGameState() != null
    }

    suspend fun deleteSavedGame() {
        dao.deleteGameState()
    }

    fun getGameStateFlow(): Flow<GameState?> = flow {
        val gameState = loadGameState()
        emit(gameState)
    }
}
