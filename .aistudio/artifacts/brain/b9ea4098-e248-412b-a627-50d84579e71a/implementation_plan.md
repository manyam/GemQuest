# GemQuest: RPG Family Chore & Reward Tracker

GemQuest transforms household chores into an exciting RPG adventure for kids while giving parents a robust management hub with a secure PIN, chore approval queue, customizable rewards, point history tracking, weekly summary reports, and completed task notifications.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions are confirmed:
> - **App Name**: **GemQuest**
> - **Entry Screen Profile Selection**: Visual face/avatar selection showcasing Kids' Hero cards vs. the Parent/Admin Guild Master card.
> - **PIN Protection for All Profiles**: Tapping any kid profile prompts for their personalized 4-digit PIN to securely access their individual quest log, point wallet, and badges. Tapping the Parent/Admin profile requires the Master Parent PIN to access approvals, rewards management, and weekly reports.
> - **Theme & Style**: Playful RPG quest theme with hero avatars, levels, XP bars, and achievement badges.
> - **Chore Approval Workflow**: Two-phase chore flow where kids mark tasks completed ("Quest Turned In"), and points are officially credited once a parent reviews and approves ("Parent Blessing").

---

### 1. Overview & Core Concept

- **What It Does**: A family-friendly app where parents create and assign daily or recurring chores ("Quests") worth points ("Gems/XP"). When the app opens, an interactive entry screen presents cards for each child adventurer alongside the parent/admin. Tapping any profile prompts for that profile's PIN before revealing their individual chore list, balance, and rewards.
- **Target Audience / Persona**:
  - **Kids (Adventurers)**: Motivated by their personal quest log, gem balance, leveling up, badges, and tangible rewards without siblings tampering with their tasks.
  - **Parents (Guild Masters)**: Want an organized household, accountability, fraud prevention via parent approval, and clear weekly summaries.
- **Key Value**: Protects each child's individual progress with their own PIN on a shared family device, preventing sibling interference while gamifying daily responsibilities.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Entry Screen (Who is Questing Today?)**:
   - Hero welcome header with a clean grid of profile cards:
     - **Kids Section**: Illustrated hero face avatars (e.g., Leo the Knight, Maya the Sorceress, Noah the Ranger) showing name, current level, and a "Lock" icon.
     - **Parents/Admin Section**: Crowned Guild Master card with gold trim.
   - On tapping any kid or parent card, a sleek 4-digit PIN dialog/keypad opens.
   - Default PINs (pre-seeded for instant exploration: Kids `1111`, `2222`; Parent `1234`), fully customizable in settings.
2. **Kid Quest Log & Submission (Post-PIN)**:
   - Filterable quest board: *Available Quests*, *Pending Parent Approval*, and *Completed Today*.
   - Each quest card features an icon, title, point value, frequency (daily, weekly, one-time), and an "Embark / Mark Done" button.
   - When tapped, the quest moves to *Pending Approval* and triggers a local notification for the parent.
   - Kids can also view their personal point balance, XP bar, badges, and redeem custom rewards in the Bazaar.
   - Quick "Switch Adventurer / Lock" button in the top bar to return safely to the Entry Screen.
3. **Leaderboard & Level Progression**:
   - Gamified leaderboard showing all kids with rank, avatar, level title (e.g., *Novice Scout*, *Chore Champion*, *Legendary Paladin*), XP progress bar to next level, and total gems earned this week.
   - Achievement showcase with unlockable badges (e.g., *Streak Master*, *Room Cleaner I*, *Dish Dynamo*).
4. **Reward Bazaar & Redemption**:
   - Catalog of custom rewards (e.g., "1 Hour Gaming Pass - 50 Gems", "Ice Cream Trip - 100 Gems").
   - Kids request redemption; if they have sufficient gems, a redemption ticket is generated for parent review.
5. **Parent Command Center (Post-Parent PIN)**:
   - **Approval Queue**: Quick approval/rejection of completed quests and reward redemptions with one tap.
   - **Quest Manager**: Add, edit, or delete chores with points, recurrence, and assigned children.
   - **Reward Manager**: Add, edit, or delete rewards and point costs.
   - **Adventurer Balances & Ledger**: View balance history over time with an interactive point trend chart and transaction log.
   - **Weekly Summary Report**: Comprehensive stats including total chores completed this week, completion percentage, points distributed, top chore categories, and day-by-day activity bar chart.
   - **Profile & PIN Manager**: Add/edit kid names, avatar icons, and change any profile's 4-digit PIN.

#### Visual Identity & Theme
- **Aesthetic Direction**: Playful RPG Fantasy / Quest Academy. Vibrant, tactile, and rewarding with soft rounded geometry, metallic badge borders, and gem-tinted cards.
- **Color Palette**:
  - Primary: Deep Quest Indigo (`#3D2C8D`) & Royal Violet (`#6C4AB6`)
  - Accent / Gems: Radiant Amber Gold (`#FFB703`) & Emerald Green (`#06D6A0`)
  - Status Indicators: Warm Coral for pending approvals (`#FF6B6B`), Azure for active quests (`#48CAE4`)
  - Background: Soft parchment tint in light mode (`#F7F8FC`) and deep obsidian dungeon in dark mode (`#1B1A28`)
  - Surfaces: Elevated cards with subtle colored borders matching quest categories.
- **Typography & Hierarchy**:
  - Bold, friendly headline hierarchy using rounded geometry for quest titles and gem numbers.
  - Clear, accessible body text (`sp`) with high-contrast badge chips.
- **Interactive Feedback & Motion**:
  - Level-up celebration dialog with animated stars and confetti effects.
  - Tactile numeric keypad with dot indicators and haptic vibration feedback on PIN entry.
  - Spring animations when switching between profiles and completing tasks.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Per-Profile PIN Security on Entry Screen**
  - *Chosen Approach*: Every profile (Kids and Parent) has its own 4-digit PIN stored securely in the local Room database (with salt/hash). The Entry Screen presents all family members prominently with large avatars; tapping one pops a PIN verification sheet.
  - *Why*: Eliminates sibling sabotage while keeping access quick and intuitive on a shared device.
- **Decision 2: Persistence Strategy (Room Database)**
  - *Chosen Approach*: On-device Room Database with full local relational tables (`ProfileEntity`, `ChoreEntity`, `ChoreCompletionEntity`, `RewardEntity`, `RedemptionEntity`, `PointTransactionEntity`).
  - *Why*: Instantaneous, offline-first access without requiring parents or kids to sign into Google accounts. Fully functional immediately out of the box with realistic seed profiles (Leo, Maya, Parent).
- **Decision 3: Notifications & Weekly Reports**
  - *Chosen Approach*: Standard Android `NotificationManager` with notification channels for completed quests, paired with an in-app dynamic Weekly Summary engine that compiles real 7-day completion and point statistics.
  - *Why*: Reliable, immediate on-device alerts when a child submits a chore for parent blessing.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────────┐
│                    Entry Screen (Profile Wall)                  │
│       Kids' Faces (Avatars)   │   Parent / Admin Face (Crown)   │
└───────────────┬───────────────┴─────────────────┬───────────────┘
                │ Tap Kid                         │ Tap Parent
                ▼                                 ▼
      [Kid 4-Digit PIN Dialog]          [Parent PIN Dialog]
                │ Valid                           │ Valid
                ▼                                 ▼
┌───────────────────────────────┐ ┌───────────────────────────────┐
│           Kids Hub            │ │     Parent Command Center     │
│  - Individual Quest Board     │ │  - Pending Approvals Queue    │
│  - My Point Wallet & XP Bar   │ │  - Chore & Reward Management  │
│  - Leaderboard & Badges       │ │  - Weekly Summary Report      │
│  - Reward Bazaar & Redeem     │ │  - Point Balances & Ledgers   │
│  - "Lock / Switch Profile"    │ │  - PIN & Profile Settings     │
└───────────────┬───────────────┘ └───────────────┬───────────────┘
                │                                 │
                ▼                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                       ChoreViewModel                            │
│   - StateFlow<ChoreUiState> (activeProfile, quests, approvals)  │
│   - PIN verification logic & temporary session state            │
│   - Approvals, redemptions, level calculations, weekly report   │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Room Database                              │
│   - profiles (id, name, avatar, role, pin, points, total_xp)    │
│   - chores (title, points, recurrence, assigned_kid_id, icon)   │
│   - chore_completions (chore_id, kid_id, status, timestamp)     │
│   - rewards (title, cost, icon, description, active)            │
│   - reward_redemptions (reward_id, kid_id, cost, status, date)  │
│   - point_transactions (kid_id, amount, reason, timestamp)      │
└─────────────────────────────────────────────────────────────────┘
```

#### Core Database Schema
1. **`ProfileEntity`**:
   - `id`: Int (auto-generated primary key)
   - `name`: String ("Leo", "Maya", "Parent / Admin")
   - `role`: String (`"KID"`, `"PARENT"`)
   - `avatarEmoji`: String / drawable identifier (e.g. 🛡️, 🧙‍♀️, 👑)
   - `pinHash`: String (salted hash or encrypted 4-digit PIN)
   - `currentPoints`: Int (spendable balance)
   - `totalXp`: Int (lifetime experience)
   - `level`: Int
   - `colorHex`: Long
2. **`ChoreEntity`**:
   - `id`: Int
   - `title`: String
   - `description`: String
   - `points`: Int
   - `iconName`: String
   - `assignedKidId`: Int? (null = available to all)
   - `frequency`: String (`DAILY`, `WEEKLY`, `ONCE`)
   - `category`: String (`CLEANING`, `HOMEWORK`, `HYGIENE`, `PETS`)
3. **`ChoreCompletionEntity`**:
   - `id`: Int
   - `choreId`: Int
   - `kidId`: Int
   - `completedAt`: Long
   - `status`: String (`PENDING`, `APPROVED`, `REJECTED`)
   - `reviewedAt`: Long?
4. **`RewardEntity`**:
   - `id`: Int
   - `title`: String
   - `description`: String
   - `gemCost`: Int
   - `iconName`: String
   - `isAvailable`: Boolean
5. **`RewardRedemptionEntity`**:
   - `id`: Int
   - `rewardId`: Int
   - `kidId`: Int
   - `gemCost`: Int
   - `status`: String (`PENDING`, `APPROVED`, `FULFILLED`)
   - `requestedAt`: Long
6. **`PointTransactionEntity`**:
   - `id`: Int
   - `kidId`: Int
   - `amount`: Int
   - `type`: String (`EARNED`, `SPENT`, `BONUS`)
   - `description`: String
   - `timestamp`: Long
