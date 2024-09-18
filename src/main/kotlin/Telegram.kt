const val STRING_MENU = "menu"

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0
    val trainer : LearnWordsTrainer

    while (true) {
        Thread.sleep(2000)
        val telegramBotService = TelegramBotService(botToken)
        val updates: String = telegramBotService.getUpdates(updateId)

        println(updates)
        updateId = getUpdateId(updates)

        val userMessage = getUserMessage(updates)
        val chatId = getChatId(updates)
        val callbackData = getData(updates)

        if (userMessage?.lowercase() == STRING_MENU) {
            telegramBotService.sendMenu(chatId)
        }

        if (callbackData?.lowercase() == "statistics_clicked" && chatId != null)
            telegramBotService.sendMessage(chatId, "Выучено 8 из 10 слов | 80%")

    }
}

fun getUpdateId(updates: String): Int {
    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    return updateIdRegex.find(updates)?.groups?.get(1)?.value?.toInt()?.plus(1) ?: 0
}

fun getData(updates: String): String {
    val dataRegex : Regex = "\"data\":\"(.+?)\"".toRegex()
    return dataRegex.find(updates)?.groups?.get(1)?.value ?: ""
}

fun getChatId(updates: String): Int? {
    val updateIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    return updateIdRegex.find(updates)?.groups?.get(1)?.value?.toInt() ?: 0
}

fun getUserMessage(updates: String): String? {
    val updateTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    return updateTextRegex.find(updates)?.groups?.get(1)?.value ?: ""
}