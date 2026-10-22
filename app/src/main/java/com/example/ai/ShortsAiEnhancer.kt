package com.example.ai

data class AiGeneratedShortPlan(
    val title: String,
    val caption: String,
    val description: String,
    val tags: String,
    val recommendedTimeNotice: String
)

object ShortsAiEnhancer {

    private val templates = mapOf(
        "tech" to listOf(
            AiGeneratedShortPlan(
                title = "🔥 3 Insane Tech Hacks You Must Try! #Shorts",
                caption = "Save this reel right now! 🤯 These 3 smartphone and tech tricks will save you hours.",
                description = "Discover the top 3 tech secrets you probably didn't know existed! Watch till the end for the craziest one. Like and subscribe for daily tech Shorts!\n\n#Shorts #TechTips #LifeHacks #ViralTech #Android",
                tags = "Shorts, TechHacks, TipsAndTricks, Viral, SmartphoneTricks, Tech",
                recommendedTimeNotice = "Best time for Tech Shorts: 6:00 PM – 8:00 PM (High engagement)"
            ),
            AiGeneratedShortPlan(
                title = "⚡ Don't Make This Coding Mistake! #Shorts",
                caption = "Every developer does this at least once! Are you guilty too? 👇",
                description = "Avoid this fatal mistake in your development workflow. Subscribe for more clean coding tips and developer insights.\n\n#Shorts #Coding #Programming #Developer #SoftwareEngineering",
                tags = "Shorts, Coding, SoftwareDevelopment, Python, Kotlin, Tech",
                recommendedTimeNotice = "Best time for Dev content: 12:00 PM – 2:00 PM (Lunch break peak)"
            )
        ),
        "entertainment" to listOf(
            AiGeneratedShortPlan(
                title = "😱 Wait For The Plot Twist at 0:25! #Shorts",
                caption = "Nobody saw that coming! 😂 What would you do in this situation?",
                description = "Watch until the very end! Drop your reaction in the comments below. Don't forget to hit like and subscribe for more entertaining Shorts every single day!\n\n#Shorts #Viral #Trending #PlotTwist #Entertainment",
                tags = "Shorts, Viral, Trending, Comedy, Entertainment, Reaction",
                recommendedTimeNotice = "Best time for Entertainment Shorts: 7:00 PM – 10:00 PM (Prime evening scroll)"
            ),
            AiGeneratedShortPlan(
                title = "🤯 The Most Satisfying Video on the Internet! #Shorts",
                caption = "Turn on your volume for pure bliss! 🎧 Rate this from 1 to 10.",
                description = "Take a breath and enjoy this ultra-satisfying moment. Subscribe for daily calming and satisfying videos.\n\n#Shorts #Satisfying #OddlySatisfying #Relaxing #Viral",
                tags = "Shorts, Satisfying, OddlySatisfying, ASMR, ViralShorts",
                recommendedTimeNotice = "Best time for Casual/Relaxing content: 9:00 PM – 11:00 PM"
            )
        ),
        "motivation" to listOf(
            AiGeneratedShortPlan(
                title = "🏆 Stop Waiting For The Right Time! #Shorts",
                caption = "This is your reminder to take action today. Tag someone who needs to hear this! 🔥",
                description = "Success doesn't come from what you do occasionally, it comes from what you do consistently. Subscribe for daily motivation and mindset growth.\n\n#Shorts #Motivation #Mindset #Success #Inspiration",
                tags = "Shorts, Motivation, SuccessMindset, Inspiration, Discipline, Hustle",
                recommendedTimeNotice = "Best time for Motivation: 6:00 AM – 8:00 AM or 9:00 PM"
            )
        ),
        "general" to listOf(
            AiGeneratedShortPlan(
                title = "✨ You Won't Believe What Happened Next! #Shorts",
                caption = "Wait till the end to see the result! 🚀 Like & Share if you enjoyed this.",
                description = "Thanks for watching! Make sure to like, comment your thoughts, and subscribe for more amazing daily Shorts.\n\n#Shorts #YouTubeShorts #Trending #Explore #Viral",
                tags = "Shorts, YouTubeShorts, Viral, Trending, ExplorePage",
                recommendedTimeNotice = "Best general posting window: 12:00 PM or 6:30 PM"
            )
        )
    )

    fun generatePlan(keyword: String, category: String = "general"): AiGeneratedShortPlan {
        val catKey = when {
            category.contains("tech", ignoreCase = true) || keyword.contains("tech", ignoreCase = true) || keyword.contains("code", ignoreCase = true) -> "tech"
            category.contains("entertain", ignoreCase = true) || keyword.contains("funny", ignoreCase = true) || keyword.contains("comedy", ignoreCase = true) -> "entertainment"
            category.contains("motiv", ignoreCase = true) || keyword.contains("gym", ignoreCase = true) || keyword.contains("fitness", ignoreCase = true) -> "motivation"
            else -> "general"
        }

        val list = templates[catKey] ?: templates["general"]!!
        val base = list.random()

        if (keyword.isNotBlank()) {
            val formattedKeyword = keyword.trim().replaceFirstChar { it.uppercase() }
            return AiGeneratedShortPlan(
                title = "$formattedKeyword - You Won't Believe This! #Shorts",
                caption = "Everything you need to know about $formattedKeyword! 🚀 Check this out.",
                description = "Here is the ultimate breakdown of $formattedKeyword. Watch till the end and let me know your thoughts in the comments!\n\n#Shorts #$formattedKeyword #Viral #Trending #YouTubeShorts",
                tags = "$formattedKeyword, Shorts, YouTubeShorts, Trending, Viral",
                recommendedTimeNotice = base.recommendedTimeNotice
            )
        }

        return base
    }
}
