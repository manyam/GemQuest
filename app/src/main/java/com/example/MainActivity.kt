package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.EntryScreen
import com.example.ui.screens.KidDashboardScreen
import com.example.ui.screens.ParentCommandScreen
import com.example.ui.theme.GemQuestTheme
import com.example.viewmodel.GemQuestViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GemQuestTheme {
                GemQuestApp()
            }
        }
    }
}

@Composable
fun GemQuestApp(
    viewModel: GemQuestViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Request notification permission for Android 13+
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled gracefully */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Collect reactive state from ViewModel
    val activeProfile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val chores by viewModel.chores.collectAsStateWithLifecycle()
    val completions by viewModel.completions.collectAsStateWithLifecycle()
    val pendingCompletions by viewModel.pendingCompletions.collectAsStateWithLifecycle()
    val rewards by viewModel.rewards.collectAsStateWithLifecycle()
    val allRedemptions by viewModel.allRedemptions.collectAsStateWithLifecycle()
    val pendingRedemptions by viewModel.pendingRedemptions.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val weeklyReport by viewModel.weeklyReport.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    // Show Snackbars on user actions
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissMessage()
        }
    }

    // Handle Android system back button: return to EntryScreen if inside a profile
    BackHandler(enabled = activeProfile != null) {
        viewModel.lockProfile()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                activeProfile == null -> {
                    EntryScreen(
                        profiles = profiles,
                        onProfileAuthenticated = { authenticated ->
                            viewModel.loginProfile(authenticated)
                        }
                    )
                }

                activeProfile?.role == "KID" -> {
                    val kid = activeProfile!!
                    // Read updated stats from current profiles list if available
                    val liveKid = profiles.find { it.id == kid.id } ?: kid
                    KidDashboardScreen(
                        kid = liveKid,
                        allProfiles = profiles,
                        chores = chores,
                        completions = completions,
                        rewards = rewards,
                        redemptions = allRedemptions,
                        onMarkChoreDone = { choreId -> viewModel.markChoreDone(choreId) },
                        onRequestRedeem = { rewardId -> viewModel.requestRewardRedemption(rewardId) },
                        onUpdateAvatar = { newAvatar -> viewModel.updateAvatar(liveKid.id, newAvatar) },
                        onLockScreen = { viewModel.lockProfile() }
                    )
                }

                activeProfile?.role == "PARENT" -> {
                    val parent = activeProfile!!
                    val liveParent = profiles.find { it.id == parent.id } ?: parent
                    ParentCommandScreen(
                        parent = liveParent,
                        profiles = profiles,
                        chores = chores,
                        pendingCompletions = pendingCompletions,
                        rewards = rewards,
                        pendingRedemptions = pendingRedemptions,
                        transactions = transactions,
                        weeklyReport = weeklyReport,
                        onApproveChore = { compId -> viewModel.approveChore(compId) },
                        onRejectChore = { compId -> viewModel.rejectChore(compId) },
                        onApproveRedemption = { redId -> viewModel.approveRedemption(redId) },
                        onRejectRedemption = { redId -> viewModel.rejectRedemption(redId) },
                        onCreateChore = { title, desc, pts, icon, kidId, freq, cat ->
                            viewModel.createChore(title, desc, pts, icon, kidId, freq, cat)
                        },
                        onUpdateChore = { id, title, desc, pts, icon, kidId, freq, cat ->
                            viewModel.updateChore(id, title, desc, pts, icon, kidId, freq, cat)
                        },
                        onDeleteChore = { choreId -> viewModel.deleteChore(choreId) },
                        onCreateReward = { title, desc, cost, icon ->
                            viewModel.createReward(title, desc, cost, icon)
                        },
                        onDeleteReward = { rewardId -> viewModel.deleteReward(rewardId) },
                        onUpdatePin = { profileId, newPin ->
                            viewModel.updatePin(profileId, newPin)
                        },
                        onAddKidProfile = { name, emoji, title, pin ->
                            viewModel.addKidProfile(name, emoji, title, pin)
                        },
                        onDeleteProfile = { profileId ->
                            viewModel.deleteProfile(profileId)
                        },
                        onLockScreen = { viewModel.lockProfile() }
                    )
                }
            }
        }
    }
}
