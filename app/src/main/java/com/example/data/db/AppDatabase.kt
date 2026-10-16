package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ChoreCompletionEntity
import com.example.data.model.ChoreEntity
import com.example.data.model.PointTransactionEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.RewardEntity
import com.example.data.model.RewardRedemptionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProfileEntity::class,
        ChoreEntity::class,
        ChoreCompletionEntity::class,
        RewardEntity::class,
        RewardRedemptionEntity::class,
        PointTransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun choreDao(): ChoreDao
    abstract fun choreCompletionDao(): ChoreCompletionDao
    abstract fun rewardDao(): RewardDao
    abstract fun rewardRedemptionDao(): RewardRedemptionDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gemquest_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedStarterData(database)
                    }
                }
            }
        }

        suspend fun seedStarterData(database: AppDatabase) {
            val profileDao = database.profileDao()
            val choreDao = database.choreDao()
            val rewardDao = database.rewardDao()
            val completionDao = database.choreCompletionDao()
            val transactionDao = database.transactionDao()

            if (profileDao.getCount() == 0) {
                // Seed Profiles: 2 kids + 1 parent
                val now = System.currentTimeMillis()
                val oneDay = 86_400_000L

                val profiles = listOf(
                    ProfileEntity(
                        id = 1,
                        name = "Leo",
                        role = "KID",
                        avatarEmoji = "🛡️",
                        heroTitle = "Knight of the Clean Realm",
                        pin = "1111",
                        currentPoints = 120,
                        totalXp = 380,
                        level = 3,
                        colorHex = 0xFF4361EE
                    ),
                    ProfileEntity(
                        id = 2,
                        name = "Maya",
                        role = "KID",
                        avatarEmoji = "🧙‍♀️",
                        heroTitle = "Sorceress of Sparkle",
                        pin = "2222",
                        currentPoints = 185,
                        totalXp = 520,
                        level = 4,
                        colorHex = 0xFF9D4EDD
                    ),
                    ProfileEntity(
                        id = 3,
                        name = "Parent / Admin",
                        role = "PARENT",
                        avatarEmoji = "👑",
                        heroTitle = "Grand Guild Master",
                        pin = "1234",
                        currentPoints = 0,
                        totalXp = 0,
                        level = 99,
                        colorHex = 0xFFFFB703
                    )
                )
                profileDao.insertAll(profiles)

                // Seed Chores
                val chores = listOf(
                    ChoreEntity(
                        id = 1,
                        title = "Make Bed & Tidy Room",
                        description = "Tuck sheets neatly, arrange pillows, and pick up toys from the floor.",
                        points = 15,
                        iconEmoji = "🛏️",
                        frequency = "DAILY",
                        category = "BEDROOM"
                    ),
                    ChoreEntity(
                        id = 2,
                        title = "Unload the Dishwasher",
                        description = "Put silverware in tray and stack plates carefully in cupboards.",
                        points = 25,
                        iconEmoji = "🍽️",
                        frequency = "DAILY",
                        category = "KITCHEN"
                    ),
                    ChoreEntity(
                        id = 3,
                        title = "Feed & Brush the Dog",
                        description = "Fill the water bowl with fresh cold water and scoop kibble.",
                        points = 20,
                        iconEmoji = "🐕",
                        frequency = "DAILY",
                        category = "PETS"
                    ),
                    ChoreEntity(
                        id = 4,
                        title = "Complete Math & Reading",
                        description = "Finish all assigned homework pages and 20 mins of book reading.",
                        points = 35,
                        iconEmoji = "📚",
                        frequency = "DAILY",
                        category = "HOMEWORK"
                    ),
                    ChoreEntity(
                        id = 5,
                        title = "Take Out Recycling & Trash",
                        description = "Empty bedroom & kitchen bins and take blue bin to the curb.",
                        points = 30,
                        iconEmoji = "♻️",
                        frequency = "WEEKLY",
                        category = "CLEANING"
                    ),
                    ChoreEntity(
                        id = 6,
                        title = "Vacuum Living Room Rug",
                        description = "Move cushions, vacuum corners, and roll up floor cords.",
                        points = 40,
                        iconEmoji = "🧹",
                        frequency = "WEEKLY",
                        category = "CLEANING"
                    )
                )
                choreDao.insertAll(chores)

                // Seed Rewards
                val rewards = listOf(
                    RewardEntity(
                        id = 1,
                        title = "30-Min Extra Screen Time",
                        description = "Unlock 30 bonus minutes for video games, tablet, or favorite show.",
                        gemCost = 40,
                        iconEmoji = "🎮"
                    ),
                    RewardEntity(
                        id = 2,
                        title = "Ice Cream Cone Outing",
                        description = "Trip to the local ice cream shop for a double scoop with sprinkles!",
                        gemCost = 100,
                        iconEmoji = "🍦"
                    ),
                    RewardEntity(
                        id = 3,
                        title = "Pick Friday Movie & Popcorn",
                        description = "You choose the family movie and get the big bowl of butter popcorn.",
                        gemCost = 80,
                        iconEmoji = "🍿"
                    ),
                    RewardEntity(
                        id = 4,
                        title = "Stay Up 45 Mins Past Bedtime",
                        description = "Weekend pass to stay up late reading or playing board games.",
                        gemCost = 70,
                        iconEmoji = "🌙"
                    ),
                    RewardEntity(
                        id = 5,
                        title = "New Book or Small Toy",
                        description = "Pick a special comic book, novel, or LEGO minifigure.",
                        gemCost = 250,
                        iconEmoji = "🎁"
                    )
                )
                rewardDao.insertAll(rewards)

                // Seed sample history for weekly reports & point trend charts
                val completions = listOf(
                    ChoreCompletionEntity(
                        id = 1,
                        choreId = 1,
                        kidId = 1,
                        choreTitle = "Make Bed & Tidy Room",
                        points = 15,
                        completedAt = now - (oneDay * 4),
                        status = "APPROVED",
                        reviewedAt = now - (oneDay * 4) + 3600000
                    ),
                    ChoreCompletionEntity(
                        id = 2,
                        choreId = 2,
                        kidId = 1,
                        choreTitle = "Unload the Dishwasher",
                        points = 25,
                        completedAt = now - (oneDay * 3),
                        status = "APPROVED",
                        reviewedAt = now - (oneDay * 3) + 3600000
                    ),
                    ChoreCompletionEntity(
                        id = 3,
                        choreId = 4,
                        kidId = 1,
                        choreTitle = "Complete Math & Reading",
                        points = 35,
                        completedAt = now - (oneDay * 1),
                        status = "APPROVED",
                        reviewedAt = now - (oneDay * 1) + 3600000
                    ),
                    ChoreCompletionEntity(
                        id = 4,
                        choreId = 3,
                        kidId = 2,
                        choreTitle = "Feed & Brush the Dog",
                        points = 20,
                        completedAt = now - (oneDay * 3),
                        status = "APPROVED",
                        reviewedAt = now - (oneDay * 3) + 3600000
                    ),
                    ChoreCompletionEntity(
                        id = 5,
                        choreId = 4,
                        kidId = 2,
                        choreTitle = "Complete Math & Reading",
                        points = 35,
                        completedAt = now - (oneDay * 2),
                        status = "APPROVED",
                        reviewedAt = now - (oneDay * 2) + 3600000
                    ),
                    ChoreCompletionEntity(
                        id = 6,
                        choreId = 6,
                        kidId = 2,
                        choreTitle = "Vacuum Living Room Rug",
                        points = 40,
                        completedAt = now - (oneDay * 1),
                        status = "APPROVED",
                        reviewedAt = now - (oneDay * 1) + 3600000
                    ),
                    ChoreCompletionEntity(
                        id = 7,
                        choreId = 1,
                        kidId = 2,
                        choreTitle = "Make Bed & Tidy Room",
                        points = 15,
                        completedAt = now - 7200000,
                        status = "PENDING"
                    )
                )
                completionDao.insertAll(completions)

                val transactions = listOf(
                    PointTransactionEntity(
                        kidId = 1,
                        amount = 15,
                        type = "EARNED",
                        description = "Quest: Make Bed & Tidy Room",
                        timestamp = now - (oneDay * 4)
                    ),
                    PointTransactionEntity(
                        kidId = 1,
                        amount = 25,
                        type = "EARNED",
                        description = "Quest: Unload the Dishwasher",
                        timestamp = now - (oneDay * 3)
                    ),
                    PointTransactionEntity(
                        kidId = 1,
                        amount = 35,
                        type = "EARNED",
                        description = "Quest: Complete Math & Reading",
                        timestamp = now - (oneDay * 1)
                    ),
                    PointTransactionEntity(
                        kidId = 2,
                        amount = 20,
                        type = "EARNED",
                        description = "Quest: Feed & Brush the Dog",
                        timestamp = now - (oneDay * 3)
                    ),
                    PointTransactionEntity(
                        kidId = 2,
                        amount = 35,
                        type = "EARNED",
                        description = "Quest: Complete Math & Reading",
                        timestamp = now - (oneDay * 2)
                    ),
                    PointTransactionEntity(
                        kidId = 2,
                        amount = 40,
                        type = "EARNED",
                        description = "Quest: Vacuum Living Room Rug",
                        timestamp = now - (oneDay * 1)
                    )
                )
                transactionDao.insertAll(transactions)
            }
        }
    }
}
