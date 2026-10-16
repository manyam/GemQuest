package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.ChoreCompletionEntity
import com.example.data.model.ChoreEntity
import com.example.data.model.PointTransactionEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.RewardEntity
import com.example.data.model.RewardRedemptionEntity
import com.example.data.repository.GemQuestRepository
import com.example.util.NotificationHelper
import com.example.util.WeeklyReport
import com.example.util.WeeklyReportCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GemQuestViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = GemQuestRepository(application, db)

    init {
        NotificationHelper.init(application)
        viewModelScope.launch {
            AppDatabase.seedStarterData(db)
        }
    }

    private val _activeProfile = MutableStateFlow<ProfileEntity?>(null)
    val activeProfile: StateFlow<ProfileEntity?> = _activeProfile.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    val profiles: StateFlow<List<ProfileEntity>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chores: StateFlow<List<ChoreEntity>> = repository.allChores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completions: StateFlow<List<ChoreCompletionEntity>> = repository.allCompletions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingCompletions: StateFlow<List<ChoreCompletionEntity>> = repository.pendingCompletions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<RewardEntity>> = repository.allRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRedemptions: StateFlow<List<RewardRedemptionEntity>> = repository.allRedemptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingRedemptions: StateFlow<List<RewardRedemptionEntity>> = repository.pendingRedemptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<PointTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyReport: StateFlow<WeeklyReport> = combine(
        completions,
        profiles
    ) { compList, profList ->
        WeeklyReportCalculator.calculate(compList, profList)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        WeeklyReport(0, 0, "None", 0, 0, emptyList(), emptyMap())
    )

    fun loginProfile(profile: ProfileEntity) {
        _activeProfile.value = profile
    }

    fun lockProfile() {
        _activeProfile.value = null
    }

    fun dismissMessage() {
        _userMessage.value = null
    }

    fun markChoreDone(choreId: Int) {
        val kid = _activeProfile.value ?: return
        viewModelScope.launch {
            val result = repository.markChoreDone(kid.id, choreId)
            result.onSuccess {
                _userMessage.value = "Quest submitted for parent blessing! 📜"
            }.onFailure {
                _userMessage.value = "Failed to submit quest: ${it.message}"
            }
        }
    }

    fun approveChore(completionId: Int) {
        viewModelScope.launch {
            val result = repository.approveChore(completionId)
            result.onSuccess {
                _userMessage.value = "Quest approved & gems credited! 💎"
                // Update active profile if it's currently logged in
                _activeProfile.value?.let { current ->
                    val updated = db.profileDao().getProfileById(current.id)
                    if (updated != null) _activeProfile.value = updated
                }
            }.onFailure {
                _userMessage.value = "Approval error: ${it.message}"
            }
        }
    }

    fun rejectChore(completionId: Int) {
        viewModelScope.launch {
            val result = repository.rejectChore(completionId)
            result.onSuccess {
                _userMessage.value = "Quest sent back for completion."
            }
        }
    }

    fun requestRewardRedemption(rewardId: Int) {
        val kid = _activeProfile.value ?: return
        viewModelScope.launch {
            val result = repository.requestRedemption(kid.id, rewardId)
            result.onSuccess {
                _userMessage.value = "Redemption requested! Parent notified. 🎁"
            }.onFailure {
                _userMessage.value = it.message ?: "Redemption error"
            }
        }
    }

    fun approveRedemption(redemptionId: Int) {
        viewModelScope.launch {
            val result = repository.approveRedemption(redemptionId)
            result.onSuccess {
                _userMessage.value = "Reward approved & gems deducted! 🎉"
                _activeProfile.value?.let { current ->
                    val updated = db.profileDao().getProfileById(current.id)
                    if (updated != null) _activeProfile.value = updated
                }
            }.onFailure {
                _userMessage.value = it.message ?: "Redemption approval error"
            }
        }
    }

    fun rejectRedemption(redemptionId: Int) {
        viewModelScope.launch {
            val result = repository.rejectRedemption(redemptionId)
            result.onSuccess {
                _userMessage.value = "Redemption declined."
            }
        }
    }

    fun createChore(
        title: String,
        description: String,
        points: Int,
        iconEmoji: String,
        assignedKidId: Int?,
        frequency: String,
        category: String
    ) {
        viewModelScope.launch {
            val result = repository.addChore(
                title, description, points, iconEmoji, assignedKidId, frequency, category
            )
            result.onSuccess {
                _userMessage.value = "New quest added to Guild board! ⚔️"
            }
        }
    }

    fun updateChore(
        choreId: Int,
        title: String,
        description: String,
        points: Int,
        iconEmoji: String,
        assignedKidId: Int?,
        frequency: String,
        category: String
    ) {
        viewModelScope.launch {
            val result = repository.updateChore(
                choreId, title, description, points, iconEmoji, assignedKidId, frequency, category
            )
            result.onSuccess {
                _userMessage.value = "Quest updated successfully! ⚔️"
            }
        }
    }

    fun deleteChore(choreId: Int) {
        viewModelScope.launch {
            val result = repository.deleteChore(choreId)
            result.onSuccess {
                _userMessage.value = "Quest removed."
            }
        }
    }

    fun createReward(
        title: String,
        description: String,
        gemCost: Int,
        iconEmoji: String
    ) {
        viewModelScope.launch {
            val result = repository.addReward(title, description, gemCost, iconEmoji)
            result.onSuccess {
                _userMessage.value = "New reward listed in the Bazaar! 🎁"
            }
        }
    }

    fun deleteReward(rewardId: Int) {
        viewModelScope.launch {
            val result = repository.deleteReward(rewardId)
            result.onSuccess {
                _userMessage.value = "Reward removed from Bazaar."
            }
        }
    }

    fun updatePin(profileId: Int, newPin: String) {
        viewModelScope.launch {
            val result = repository.updateProfilePin(profileId, newPin)
            result.onSuccess {
                _userMessage.value = "PIN updated successfully!"
            }
        }
    }

    fun addKidProfile(name: String, avatarEmoji: String, heroTitle: String, pin: String) {
        viewModelScope.launch {
            val colors = listOf(0xFF4361EEL, 0xFF9D4EDDL, 0xFF06D6A0L, 0xFFFFB703L, 0xFFFF6B6BL)
            val color = colors.random()
            val result = repository.addKidProfile(name, avatarEmoji, heroTitle, pin, color)
            result.onSuccess {
                _userMessage.value = "Welcome new adventurer $name! 🛡️"
            }
        }
    }

    fun updateAvatar(profileId: Int, newAvatarEmoji: String) {
        viewModelScope.launch {
            val result = repository.updateAvatar(profileId, newAvatarEmoji)
            result.onSuccess {
                _userMessage.value = "Avatar updated! Looking heroic! ✨"
                _activeProfile.value?.let { current ->
                    if (current.id == profileId) {
                        _activeProfile.value = current.copy(avatarEmoji = newAvatarEmoji)
                    }
                }
            }
        }
    }

    fun deleteProfile(profileId: Int) {
        viewModelScope.launch {
            val result = repository.deleteProfile(profileId)
            result.onSuccess {
                _userMessage.value = "Profile removed from the Guild."
                if (_activeProfile.value?.id == profileId) {
                    _activeProfile.value = null
                }
            }
        }
    }
}
