package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("GemQuest", appName)
    }

    @Test
    fun `database in-memory seeds profiles and chores`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        AppDatabase.seedStarterData(db)

        val profileCount = db.profileDao().getCount()
        assertTrue(profileCount >= 3)

        val choreCount = db.choreDao().getCount()
        assertTrue(choreCount >= 6)
        val firstChore = db.choreDao().getChoreById(1)
        assertNotNull(firstChore)
        assertTrue(firstChore?.title?.contains("Bed") == true)

        val rewardCount = db.rewardDao().getCount()
        assertTrue(rewardCount >= 5)
        val firstReward = db.rewardDao().getRewardById(1)
        assertNotNull(firstReward)
        assertTrue(firstReward?.title?.contains("Screen Time") == true)

        db.close()
    }
}
