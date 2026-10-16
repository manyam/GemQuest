# ⚔️ GemQuest — Gamified Family Chore & Reward Tracker 💎

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Room-SQLite%20Persistence-3DDC84.svg?logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Tests](https://img.shields.io/badge/Unit%20Tests-Passing%20(Robolectric)-success.svg)](https://robolectric.org/)

**GemQuest** transforms everyday household responsibilities into an engaging RPG adventure. Children step into the shoes of **Adventurers**, completing daily quests to earn shiny **Gems** and **XP**, level up their hero titles (from *Novice Recruit* to *Legendary Master of Quests*), unlock milestone badges, and redeem exciting rewards in the **Loot Vault**. Parents manage household operations through a secure PIN-guarded **Parent Command Center** equipped with automated keyword icon matching, one-tap approvals, custom rewards, and weekly analytics reports.

---

## 📑 Table of Contents
1. [Architecture Overview](#-architecture-overview)
2. [Data Layer & Domain Models](#-data-layer--domain-models)
3. [Data Flows & Life Cycles](#-data-flows--life-cycles)
4. [Sample Screenshots & Screen Wireframes](#-sample-screenshots--screen-wireframes)
5. [Key Feature Highlights](#-key-feature-highlights)
6. [Test Case Coverage & Testing Strategy](#-test-case-coverage--testing-strategy)
7. [Directory Structure](#-directory-structure)
8. [Getting Started & Build Guide](#-getting-started--build-guide)

---

## 🏛️ Architecture Overview

GemQuest follows modern Android development practices using **Clean Architecture** and the **MVVM (Model-View-ViewModel)** design pattern with a fully reactive, unidirectional data flow (UDF).

```
                      ┌──────────────────────────────────────┐
                      │          UI Layer (Compose)          │
                      │  EntryScreen  •  KidDashboardScreen  │
                      │  ParentCommandScreen • Dialogs/Pads  │
                      └──────────────────┬───────────────────┘
                                         │ State Observation
                                         │ User Actions
                                         ▼
                      ┌──────────────────────────────────────┐
                      │           ViewModel Layer            │
                      │         GemQuestViewModel            │
                      │  (Holds StateFlow<GemQuestUiState>)  │
                      └──────────────────┬───────────────────┘
                                         │ Coroutine Dispatchers
                                         ▼
                      ┌──────────────────────────────────────┐
                      │          Repository Layer            │
                      │        GemQuestRepository            │
                      │  • Offline-first Single Source Truth │
                      │  • Local Notification Dispatches     │
                      └──────────┬───────────────────┬───────┘
                                 │                   │
                   Domain Logic  ▼                   ▼  Data Persistence
        ┌──────────────────────────────┐       ┌────────────────────────┐
        │        Utility Engines       │       │    AppDatabase (Room)  │
        │ • GamificationEngine         │       │ • ProfileDao           │
        │ • QuestIconMatcher           │       │ • ChoreDao             │
        │ • WeeklyReportCalculator     │       │ • ChoreCompletionDao   │
        │ • NotificationHelper         │       │ • RewardDao & TxDao    │
        └──────────────────────────────┘       └────────────────────────┘
```

### Architectural Principles
- **Unidirectional Data Flow (UDF)**: The UI emits events/intents to the `GemQuestViewModel`, which executes repository business logic and publishes immutable `GemQuestUiState` updates via Kotlin `StateFlow`.
- **Single Source of Truth**: The local Room SQLite database (`AppDatabase`) serves as the authoritative source of truth. Changes trigger reactive database queries that automatically notify listeners.
- **Offline First**: All user actions, quest creations, status reviews, and profile modifications operate locally without network latency.
- **Defensive Data Separation**: Role-based access ensures children cannot alter point totals or bypass approvals, while parents use secure 4-digit PIN authentication.

---

## 💾 Data Layer & Domain Models

### Room SQLite Database Schema
The Room database consists of six core entities managed in `Entities.kt`:

| Table Name | Primary Entity | Key Fields & Purpose |
| :--- | :--- | :--- |
| `profiles` | `ProfileEntity` | `id`, `name`, `role` (`KID`\|`PARENT`), `avatarEmoji`, `heroTitle`, `pin`, `currentPoints`, `totalXp`, `level`, `colorHex` |
| `chores` | `ChoreEntity` | `id`, `title`, `description`, `points`, `iconEmoji`, `assignedKidId`, `frequency`, `category`, `isArchived` |
| `chore_completions` | `ChoreCompletionEntity` | `id`, `choreId`, `kidId`, `choreTitle`, `points`, `completedAt`, `status` (`PENDING`\|`APPROVED`\|`REJECTED`), `reviewedAt` |
| `rewards` | `RewardEntity` | `id`, `title`, `description`, `gemCost`, `iconEmoji`, `isArchived` |
| `reward_redemptions` | `RewardRedemptionEntity` | `id`, `rewardId`, `kidId`, `rewardTitle`, `gemCost`, `status` (`PENDING`\|`APPROVED`\|`REJECTED`), `requestedAt`, `reviewedAt` |
| `point_transactions`| `PointTransactionEntity` | `id`, `kidId`, `amount`, `type` (`EARNED`\|`SPENT`\|`BONUS`), `description`, `timestamp` |

---

## 🔄 Data Flows & Life Cycles

### 1. Quest Creation Flow (with Real-Time Keyword Matching)
```
Parent types Quest Title / Instructions
       │
       ▼
QuestIconMatcher evaluates keywords (e.g., "dishwasher", "laundry", "dog")
       │
       ▼
Calculates scores across 38+ categories and 200+ chore keywords
       │
       ▼
Top matching icons dynamically displayed with keyword badges
       │
       ▼
Parent taps "Save Quest" ──> GemQuestRepository.addChore(...)
       │
       ▼
Room inserts ChoreEntity ──> StateFlow emits updated chore list to Kid & Parent screens
```

### 2. Quest Turn-In & Parent Approval Flow
```
1. Kid taps "Complete Quest" on their dashboard
2. ChoreCompletionEntity is inserted with status = "PENDING"
3. NotificationHelper triggers system notification:
   "📜 Quest Turned In by [KidName]! Waiting for Parent Approval."
4. Parent opens Command Center -> Approvals Tab
5. Under "Turned In Quests", Parent reviews completion and taps "Approve" (or "Reject")
6. Upon "Approve":
   - Completion status changes to "APPROVED"
   - Kid profile receives points (Gems) and XP
   - GamificationEngine recalculates Level and Title
   - PointTransactionEntity logged as EARNED
   - Notification sent: "💎 Quest Approved! You earned +[X] Gems!"
```

### 3. Loot Vault & Reward Fulfillment Flow
```
1. Kid opens "Loot Vault" on their dashboard
2. If kid has sufficient gems (e.g., 40 gems for "30-Min Extra Screen Time"):
   - Kid taps "Claim Reward"
   - RewardRedemptionEntity inserted with status = "PENDING"
3. Parent opens Command Center -> Approvals Tab
4. Under "Reward Claims", Parent taps "Fulfill"
5. Kid's gem balance is decremented and PointTransaction logged as SPENT
```

### 4. Profile Management & Kid Avatar Customization Flow
```
1. Entry Screen lists all kid profiles with their custom avatars
2. Kid or parent taps the Avatar Badge to trigger AvatarPickerDialog
3. 24 high-expression RPG fantasy avatar choices available
4. Selection calls repository.updateAvatar(...) and instantly syncs across all screens
5. "Add a Kid" button is positioned cleanly below the list with customizable name, title, PIN, and avatar
6. Parents can also edit or delete profiles directly inside Parent Command Center
```

---

## 📱 Sample Screenshots & Screen Wireframes

### Screen 1: Entry / Role Selection (`EntryScreen.kt`)
```
+-------------------------------------------------------+
|                     GemQuest ⚔️                       |
|         Gamified Family Chores & Rewards             |
|                                                       |
|  Select Your Profile:                                 |
|                                                       |
|  +-------------------------------------------------+  |
|  |  [ 🛡️ ] Leo              Level 3 • 120 💎       |  |
|  |         Knight of the Clean Realm       (Tap >) |  |
|  +-------------------------------------------------+  |
|                                                       |
|  +-------------------------------------------------+  |
|  |  [ 🧙‍♀️ ] Maya             Level 4 • 185 💎       |  |
|  |         Sorceress of Sparkle            (Tap >) |  |
|  +-------------------------------------------------+  |
|                                                       |
|        [ + Add a Kid Profile ]                        |
|                                                       |
|  ---------------------------------------------------  |
|  [ 👑 Parent HQ ] (Requires 4-Digit Security PIN)     |
+-------------------------------------------------------+
```

### Screen 2: Kid Adventurer Dashboard (`KidDashboardScreen.kt`)
```
+-------------------------------------------------------+
| [ 🛡️ Leo ] Level 3 Knight of Order         [ Switch ] |
|                                                       |
| [ 💎 120 Gems ]   [ 🔥 3-Day Streak ]   [ 🛡️ Shield ] |
| Level 3 Progress: [========--------] 65% to Level 4   |
|                                                       |
| [ Active Quests (3) ]          [ 🎁 Loot Vault (5) ]  |
|                                                       |
| +---------------------------------------------------+ |
| | 🍽️  Unload the Dishwasher                +25 💎   | |
| |     Stack silverware and put plates away          | |
| |     [ Mark as Done ⚔️ ]                           | |
| +---------------------------------------------------+ |
| | 🛏️  Make Bed & Tidy Room                 +15 💎   | |
| |     Status: ⏳ Waiting for Parent Approval        | |
| +---------------------------------------------------+ |
| | 📚  Complete Math & Reading              +35 💎   | |
| |     Status: ✅ Quest Completed & Approved         | |
| +---------------------------------------------------+ |
|                                                       |
| Recent Badges: [ 🎯 First Step ] [ 🧹 Clean Sweep ]   |
+-------------------------------------------------------+
```

### Screen 3: Parent Command Center (`ParentCommandScreen.kt`)
```
+-------------------------------------------------------+
| Parent Command Center 👑                    [ Save ]  |
|                                                       |
| [ ⚔️ Quests ]  [ 🔔 Approvals (2) ]  [ 🎁 Loot ] [ 📊 ]|
|                                                       |
| Turned In Quests (1)                                  |
| +---------------------------------------------------+ |
| | 🛡️  Unload the Dishwasher                         | |
| |     Completed by Leo • +25 Gems                   | |
| |                                                   | |
| |     [ ❌ Reject ]             [ ✅ Approve ]      | |
| +---------------------------------------------------+ |
|                                                       |
| Reward Claims (1)                                     |
| +---------------------------------------------------+ |
| | 🎁  30-Min Extra Screen Time                      | |
| |     Claimed by Maya • Cost: 40 Gems               | |
| |                                                   | |
| |     [ ❌ Decline ]            [ 🎁 Fulfill ]      | |
| +---------------------------------------------------+ |
+-------------------------------------------------------+
```

### Screen 4: Quest Keyword Icon Picker (`QuestIconPicker.kt`)
```
+-------------------------------------------------------+
| Quest Title: "Wash dishes after dinner"               |
|                                                       |
| Matching Icons for "Wash dishes after dinner":        |
| +---------------------------------------------------+ |
| | [🍽️ Dishes]    [🧽 Clean Table]    [🍳 Kitchen]    | |
| | (dishwasher)   (counter)           (cook)          | |
| +---------------------------------------------------+ |
|                                                       |
| Selected Icon: [ 🍽️ Dishes ] (Auto-matched)          |
|                                                       |
| [v Browse All 38+ Quest Icons Library]                |
+-------------------------------------------------------+
```

---

## 🌟 Key Feature Highlights

1. **Smart Keyword Icon Detection (`QuestIconMatcher`)**
   - Real-time linguistic matching across 38+ chore categories.
   - Over 200 keywords mapped (e.g., *sink, dishes, bed, laundry, dog, cat, vacuum, mop, teeth, garden, trash*).
   - Weighted score ranking with auto-selection of top match while leaving full choice to the user.

2. **RPG Progression & Leveling Engine (`GamificationEngine`)**
   - Quadratic XP curve ensuring satisfying, balanced level-ups.
   - Dynamic titles for adventurers from Level 1 (*Novice Recruit*) up to Level 8 (*Legendary Master of Quests*).
   - Milestone badges automatically awarded: *First Step*, *Sweep Champion*, *Gem Hoarder*, *Order Knight*, *Treasure Claimer*, and *Master of Deeds*.

3. **Weekly Analytics & Family Summary (`WeeklyReportCalculator`)**
   - 7-day rolling window of completed chores and gem distributions.
   - Highlights the week's **Top Adventurer**.
   - Household chore category distribution (Bedroom, Kitchen, Pets, Homework, Cleaning).

4. **Multi-Kid Household & Privacy**
   - Separate profiles for each child with customized avatars and theme color accents.
   - Individual PIN protection for profiles.
   - Kid self-avatar selection dialog with 24 expressive emojis.

5. **Parent Governance & Security**
   - 4-digit Master PIN protection for Parent HQ.
   - Full quest CRUD (Add, Edit with pre-filled inputs, Archive/Delete).
   - Verification gate for all points: child submissions require parent approval before gems are credited.

---

## 🧪 Test Case Coverage & Testing Strategy

GemQuest features comprehensive local JVM testing utilizing standard **JUnit 4** and **Robolectric** (simulating Android SDK 36 on the local JVM without requiring a physical device or emulator).

### Test Suite Execution
Tests can be executed directly from Gradle:
```bash
gradle :app:testDebugUnitTest
```

### Test Case Breakdown

| Test File | Test Case Method | Target Component | Verification Objective |
| :--- | :--- | :--- | :--- |
| `ExampleUnitTest.kt` | `gamification_levelCalculation` | `GamificationEngine` | Verifies quadratic XP to level mapping (0 XP -> Lvl 1, 40 XP -> Lvl 2, 160 XP -> Lvl 3). |
| `ExampleUnitTest.kt` | `gamification_heroTitle` | `GamificationEngine` | Validates hero title assignments for kid roles and parent guild master roles. |
| `ExampleUnitTest.kt` | `gamification_badgesUnlockCorrectly` | `GamificationEngine` | Verifies unlock triggers for milestone badges based on XP, balance, and completions. |
| `ExampleUnitTest.kt` | `gamification_levelProgressFraction` | `GamificationEngine` | Validates progress bar float fractions within level floors and ceilings. |
| `ExampleUnitTest.kt` | `weeklyReport_aggregatesCorrectly` | `WeeklyReportCalculator` | Verifies weekly gem sums, top kid identification, and chore count calculations. |
| `ExampleUnitTest.kt` | `weeklyReport_handlesEmptyData` | `WeeklyReportCalculator` | Tests edge case resilience when no completions exist (zero division safety). |
| `ExampleUnitTest.kt` | `questIconMatcher_matchesKeywords` | `QuestIconMatcher` | Tests keyword detection for dishes (`🍽️`), dog walking (`🐕`), bed tidy (`🛏️`), and homework (`📚`). |
| `ExampleUnitTest.kt` | `questIconMatcher_scoredSorting` | `QuestIconMatcher` | Verifies relevance-based ranking and top match selection for complex query strings. |
| `ExampleUnitTest.kt` | `questIconMatcher_popularIconsFallback` | `QuestIconMatcher` | Confirms fallback availability of standard common household chore icons. |
| `ExampleRobolectricTest.kt` | `read string from context` | Android Resources | Verifies Android context string resolution for `R.string.app_name` ("GemQuest"). |
| `ExampleRobolectricTest.kt` | `database in-memory seeds profiles and chores` | `AppDatabase` & DAOs | Tests Room database in-memory initialization, entity insertion, and seed data verification. |

---

## 📂 Directory Structure

```
GemQuest/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt                # App Entrypoint & Root Scaffold
│   │   │   │   ├── data/
│   │   │   │   │   ├── db/
│   │   │   │   │   │   ├── AppDao.kt              # Room DAOs (Profiles, Chores, etc.)
│   │   │   │   │   │   └── AppDatabase.kt         # Room Database & Seed Data
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── Entities.kt            # Room Entity definitions
│   │   │   │   │   └── repository/
│   │   │   │   │       └── GemQuestRepository.kt  # Offline-first Data Repository
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── AvatarPickerDialog.kt  # 24-Avatar Selection Dialog
│   │   │   │   │   │   ├── GamificationComponents.kt # Badges, Streak Flames, Level Bars
│   │   │   │   │   │   ├── PinPadDialog.kt        # 4-Digit Security PIN Pad
│   │   │   │   │   │   ├── PointHistoryComponents.kt # Transaction History & Analytics
│   │   │   │   │   │   └── QuestIconPicker.kt     # Keyword Matching Icon Picker
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── EntryScreen.kt         # Profile Picker & Role Selection
│   │   │   │   │   │   ├── KidDashboardScreen.kt  # Quests, Loot Vault & Streaks
│   │   │   │   │   │   └── ParentCommandScreen.kt # Quests CRUD, Approvals, Analytics
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt               # M3 GemQuest Palette (Gold, Purple, Emerald)
│   │   │   │   │       ├── Theme.kt               # Dynamic Light / Dark Theming
│   │   │   │   │       └── Type.kt                # Typography specs
│   │   │   │   ├── util/
│   │   │   │   │   ├── GamificationEngine.kt      # XP, Level Curve & Badges
│   │   │   │   │   ├── NotificationHelper.kt      # Push Notifications for Turns & Approvals
│   │   │   │   │   ├── QuestIconMatcher.kt        # 38+ Category Icon Keyword Engine
│   │   │   │   │   └── WeeklyReportCalculator.kt  # 7-Day Performance Analytics
│   │   │   │   └── viewmodel/
│   │   │   │       └── GemQuestViewModel.kt       # ViewModel & UI State Holder
│   │   │   └── res/                               # Icons, Drawables, Mipmaps, Strings
│   │   └── test/java/com/example/
│   │       ├── ExampleUnitTest.kt                 # Unit Tests for Engines & Calculators
│   │       └── ExampleRobolectricTest.kt          # Robolectric JVM Database & Context Tests
│   └── build.gradle.kts
├── metadata.json                                  # AI Studio App Manifest
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Getting Started & Build Guide

### Prerequisites
- **Android Studio**: Ladybug / Hedgehog or newer
- **JDK**: OpenJDK 17 or 21
- **Target SDK**: Android 35 / 36
- **Min SDK**: Android 26 (Android 8.0 Oreo+)

### Running Locally
1. Clone or download the repository into your Android workspace.
2. Open the project root in Android Studio.
3. Allow Gradle to sync dependencies.
4. Select an Android emulator (API 26+) or a connected physical Android device.
5. Click **Run** (`Shift + F10`) or execute:
   ```bash
   gradle :app:installDebug
   ```

### Default Credentials
- **Parent PIN**: `1234` (Can be modified inside Parent Command Center)
- **Sample Kid Profiles**:
  - `Leo` (Knight of the Clean Realm, Default PIN: `1111`)
  - `Maya` (Sorceress of Sparkle, Default PIN: `2222`)
