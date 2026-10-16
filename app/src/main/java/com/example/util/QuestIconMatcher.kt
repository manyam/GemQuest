package com.example.util

data class QuestIconInfo(
    val icon: String,
    val label: String,
    val keywords: List<String>
)

data class ScoredQuestIcon(
    val iconInfo: QuestIconInfo,
    val score: Int,
    val matchedKeyword: String?
)

object QuestIconMatcher {
    val ALL_QUEST_ICONS: List<QuestIconInfo> = listOf(
        QuestIconInfo("🛏️", "Bed", listOf("bed", "make bed", "sheets", "pillow", "blanket", "sleep", "wake", "morning", "tidy bed", "bedroom")),
        QuestIconInfo("🍽️", "Dishes", listOf("dish", "dishes", "plate", "plates", "dishwasher", "sink", "fork", "spoon", "table", "clear", "clear table", "dinner", "lunch", "breakfast", "kitchen", "load dishwasher", "unload dishwasher", "pots", "pans")),
        QuestIconInfo("🧽", "Wipe / Counter", listOf("wipe", "counter", "sponge", "table", "clean table", "wipe down", "surface", "clean counter", "sink")),
        QuestIconInfo("🐕", "Dog", listOf("dog", "puppy", "bark", "walk dog", "feed dog", "pet dog", "leash", "walk the dog", "poop", "dog food", "canine")),
        QuestIconInfo("🐈", "Cat", listOf("cat", "kitten", "litter", "meow", "feed cat", "cat food", "litter box", "clean litter", "feline")),
        QuestIconInfo("🐟", "Fish", listOf("fish", "tank", "aquarium", "feed fish", "bowl")),
        QuestIconInfo("🐹", "Hamster / Pet", listOf("hamster", "guinea pig", "bird", "rabbit", "bunny", "cage", "small pet", "cage clean")),
        QuestIconInfo("🧹", "Sweep / Clean", listOf("clean", "sweep", "broom", "dust", "tidy", "floor", "living room", "hallway", "clean up", "mess", "organize")),
        QuestIconInfo("🪣", "Mop / Scrub", listOf("mop", "bucket", "scrub", "wash floor", "mop floor")),
        QuestIconInfo("🔌", "Vacuum", listOf("vacuum", "hoover", "carpet", "rug", "vacuuming", "vacuum living room")),
        QuestIconInfo("🧺", "Laundry", listOf("laundry", "clothes", "wash", "fold", "basket", "dryer", "washer", "hang", "fold clothes", "hamper", "put away laundry")),
        QuestIconInfo("👕", "Closet / Clothes", listOf("shirt", "pants", "closet", "drawer", "wardrobe", "dress", "hang up", "jacket", "coat", "socks", "dress up")),
        QuestIconInfo("👟", "Shoes", listOf("shoes", "shoe", "tie shoes", "boots", "sneakers", "put shoes away")),
        QuestIconInfo("🗑️", "Trash", listOf("trash", "garbage", "rubbish", "bin", "waste", "take out", "take out trash", "empty bin", "empty trash")),
        QuestIconInfo("♻️", "Recycle", listOf("recycle", "recycling", "blue bin", "cardboard", "plastic", "can", "cans", "bottles", "sorting")),
        QuestIconInfo("📚", "Homework", listOf("homework", "study", "math", "read", "book", "school", "science", "english", "history", "quiz", "reading", "read book", "assignment", "spelling")),
        QuestIconInfo("✏️", "Writing / Art", listOf("write", "pencil", "essay", "practice", "draw", "art", "paint", "coloring", "sketch", "handwriting", "journal")),
        QuestIconInfo("🎒", "Backpack", listOf("backpack", "pack", "bag", "school bag", "prep", "pack bag", "school prep", "supplies")),
        QuestIconInfo("🪴", "Plants", listOf("plant", "plants", "flower", "flowers", "water plants", "garden", "gardening", "water", "pot", "houseplants")),
        QuestIconInfo("🌿", "Yard / Lawn", listOf("yard", "grass", "mow", "leaves", "rake", "weeds", "outdoor", "lawn", "pull weeds", "shovel", "rake leaves")),
        QuestIconInfo("🪥", "Brush Teeth", listOf("teeth", "brush teeth", "toothbrush", "floss", "dental", "brushing", "mouthwash")),
        QuestIconInfo("🛁", "Bath / Shower", listOf("bath", "shower", "soap", "shampoo", "hygiene", "towel", "wash hands", "take bath", "take shower", "hair")),
        QuestIconInfo("🧸", "Toys", listOf("toy", "toys", "lego", "teddy", "doll", "blocks", "playroom", "clean toys", "pick up toys", "put away toys")),
        QuestIconInfo("🎮", "Gaming", listOf("game", "gaming", "controller", "video game", "console", "screen time", "device")),
        QuestIconInfo("🍳", "Cooking", listOf("cook", "bake", "breakfast", "eggs", "meal", "stove", "baking", "prep dinner", "make snack", "help cook")),
        QuestIconInfo("🥪", "Lunch / Snack", listOf("lunch", "sandwich", "pack lunch", "snack", "eat lunch", "healthy snack")),
        QuestIconInfo("🥛", "Water / Drink", listOf("water bottle", "hydrate", "drink water", "milk", "drink")),
        QuestIconInfo("🍎", "Healthy Eat", listOf("fruit", "apple", "vegetables", "healthy", "salad", "eat greens", "eat veggies")),
        QuestIconInfo("🚗", "Car Wash", listOf("car", "wash car", "vehicle", "garage", "clean car", "vacuum car")),
        QuestIconInfo("🚲", "Bicycle", listOf("bike", "bicycle", "scooter", "helmet", "ride", "pump tires")),
        QuestIconInfo("🎵", "Music", listOf("music", "piano", "guitar", "violin", "instrument", "practice music", "flute", "drums", "sing", "band")),
        QuestIconInfo("⚽", "Sports", listOf("soccer", "football", "ball", "sport", "practice", "exercise", "workout", "gym", "baseball", "basketball", "tennis", "swim", "swimming")),
        QuestIconInfo("📮", "Mail", listOf("mail", "letter", "mailbox", "package", "check mail", "bring mail")),
        QuestIconInfo("🛌", "Bedtime", listOf("nap", "quiet time", "sleep early", "bedtime", "rest", "lights out")),
        QuestIconInfo("⏰", "Wake / Alarm", listOf("alarm", "wake up", "on time", "early", "routine", "morning routine")),
        QuestIconInfo("⚔️", "Hero Quest", listOf("quest", "hero", "challenge", "mission", "special", "adventure", "level up", "epic")),
        QuestIconInfo("⭐", "Kindness / Star", listOf("star", "behavior", "good deed", "polite", "listen", "kindness", "manners", "helping", "respect")),
        QuestIconInfo("✨", "General Chore", listOf("sparkle", "chore", "help", "order", "assist", "bonus", "tidy up"))
    )

    val POPULAR_QUEST_ICONS: List<QuestIconInfo> = listOf(
        ALL_QUEST_ICONS.first { it.icon == "🧹" },
        ALL_QUEST_ICONS.first { it.icon == "🛏️" },
        ALL_QUEST_ICONS.first { it.icon == "🍽️" },
        ALL_QUEST_ICONS.first { it.icon == "🐕" },
        ALL_QUEST_ICONS.first { it.icon == "🧺" },
        ALL_QUEST_ICONS.first { it.icon == "📚" },
        ALL_QUEST_ICONS.first { it.icon == "🪥" },
        ALL_QUEST_ICONS.first { it.icon == "🧸" },
        ALL_QUEST_ICONS.first { it.icon == "🗑️" },
        ALL_QUEST_ICONS.first { it.icon == "🪴" },
        ALL_QUEST_ICONS.first { it.icon == "🎒" },
        ALL_QUEST_ICONS.first { it.icon == "⭐" }
    )

    fun getScoredMatches(text: String): List<ScoredQuestIcon> {
        val clean = text.trim().lowercase()
        if (clean.isBlank()) {
            return emptyList()
        }

        val words = clean.split("[\\s,\\.\\?!\\-_/]+".toRegex()).filter { it.length >= 2 }
        val scored = mutableListOf<ScoredQuestIcon>()

        for (item in ALL_QUEST_ICONS) {
            var score = 0
            var bestMatchedKw: String? = null

            for (kw in item.keywords) {
                val kwLower = kw.lowercase()
                // Check if exact multi-word phrase is contained
                if (clean.contains(kwLower)) {
                    score += 20
                    if (bestMatchedKw == null) bestMatchedKw = kw
                }
                for (w in words) {
                    if (kwLower == w) {
                        score += 15
                        if (bestMatchedKw == null) bestMatchedKw = kw
                    } else if (w.length >= 3 && kwLower.startsWith(w)) {
                        score += 8
                        if (bestMatchedKw == null) bestMatchedKw = kw
                    } else if (kwLower.length >= 4 && w.contains(kwLower)) {
                        score += 6
                        if (bestMatchedKw == null) bestMatchedKw = kw
                    }
                }
            }
            if (score > 0) {
                scored.add(ScoredQuestIcon(item, score, bestMatchedKw))
            }
        }

        return scored.sortedByDescending { it.score }
    }

    /**
     * Returns ONLY the list of QuestIconInfo items that matched the keywords in text.
     */
    fun getMatchingIconInfos(text: String): List<QuestIconInfo> {
        return getScoredMatches(text).map { it.iconInfo }
    }

    /**
     * Returns the list of icon emojis, with matching icons appearing first.
     */
    fun getMatchedIcons(text: String): List<String> {
        val matches = getMatchingIconInfos(text).map { it.icon }
        if (matches.isEmpty()) {
            return ALL_QUEST_ICONS.map { it.icon }
        }
        return (matches + ALL_QUEST_ICONS.map { it.icon }).distinct()
    }

    fun hasDirectMatch(text: String): Boolean {
        return getScoredMatches(text).isNotEmpty()
    }
}
