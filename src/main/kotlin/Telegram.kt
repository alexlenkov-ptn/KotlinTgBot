fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)

        val telegramBotService = TelegramBotService(botToken)

        val updates: String = telegramBotService.getUpdates(updateId)

        println(updates)

        updateId = getUpdateId(updates)

        val userMessage = getUserMessage(updates)

        val chatId = getChatId(updates)

        telegramBotService.sendMessage(chatId, userMessage)
    }
}

fun getUpdateId(updates: String): Int {
    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val matchResult: MatchResult? = updateIdRegex.find(updates)
    val groups = matchResult?.groups
    return (groups?.get(1)?.value?.toInt()?.plus(1)) ?: 0
}

fun getChatId(updates: String): Int? {
    val updateIdRegex: Regex = "\"id\":(.+?),".toRegex()
    val matchResult: MatchResult? = updateIdRegex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value?.toInt() ?: 0
}

fun getUserMessage(updates: String): String? {
    val updateTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val matchResult: MatchResult? = updateTextRegex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value ?: ""
}