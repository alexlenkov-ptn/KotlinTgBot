const val STRING_MENU = "menu"
const val STRING_START = "/start"

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val telegramBotService = TelegramBotService(botToken)
        val updates: String = telegramBotService.getUpdates(updateId)

        println(updates)
        updateId = getUpdateId(updates)

        val userMessage = getUserMessage(updates)
        val chatId = getChatId(updates)
        val callbackData = getData(updates)

        if (userMessage.lowercase() == STRING_START)
            telegramBotService.sendMenu(chatId)

        if (userMessage.lowercase() == STRING_MENU)
            telegramBotService.sendMenu(chatId)

        when {
            callbackData.lowercase() == "learn_words_clicked" -> {
                telegramBotService.checkNextQuestionAndSend(trainer, chatId)
            }

            callbackData.lowercase() == "statistics_clicked" -> {
                telegramBotService.sendMessage(
                    chatId,
                    "Выучено ${trainer.getStatistics().correctAnswer} из " +
                            "${trainer.getStatistics().allElements} слов | " +
                            "${trainer.getStatistics().percentResult}%"
                )
            }

            callbackData.startsWith(CALLBACK_DATA_ANSWER_PREFIX) -> {
                telegramBotService.checkAnswer(callbackData, trainer, chatId)
                telegramBotService.checkNextQuestionAndSend(trainer, chatId)
            }

        }
    }
}

fun getUpdateId(updates: String): Int {
    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    return updateIdRegex.find(updates)?.groups?.get(1)?.value?.toInt()?.plus(1) ?: 0
}

fun getData(updates: String): String {
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()
    return dataRegex.find(updates)?.groups?.get(1)?.value ?: ""
}

fun getChatId(updates: String): Int {
    val updateIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    return updateIdRegex.find(updates)?.groups?.get(1)?.value?.toInt() ?: 0
}

fun getUserMessage(updates: String): String {
    val updateTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    return updateTextRegex.find(updates)?.groups?.get(1)?.value ?: ""
}