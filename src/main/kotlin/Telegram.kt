import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)


fun main(args: Array<String>) {
    val botToken = args[0]
    var lastUpdateId = 0L

    val trainer = LearnWordsTrainer()

    val json = Json {
        ignoreUnknownKeys = true
    }

    while (true) {
        Thread.sleep(2000)

        val telegramBotService = TelegramBotService(botToken)
        val responseString: String = telegramBotService.getUpdates(lastUpdateId)

        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1

        println(responseString)

        val userMessage = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val callbackData = firstUpdate.callbackQuery?.data

        if (userMessage?.lowercase() == STRING_START && chatId != null)
            telegramBotService.sendMenu(json, chatId)

        if (userMessage?.lowercase() == STRING_MENU && chatId != null)
            telegramBotService.sendMenu(json, chatId)

        if (callbackData != null && chatId != null) {
            when {
                callbackData.lowercase() == CALLBACK_LEARN_WORDS_CLICKED -> {
                    telegramBotService.checkNextQuestionAndSend(json, trainer, chatId)
                }

                callbackData.lowercase() == CALLBACK_STATISTICS_CLICKED -> {
                    telegramBotService.sendMessage(
                        json,
                        chatId,
                        "Выучено ${trainer.getStatistics().correctAnswer} из " +
                                "${trainer.getStatistics().allElements} слов | " +
                                "${trainer.getStatistics().percentResult}%"
                    )
                }

                callbackData.startsWith(CALLBACK_DATA_ANSWER_PREFIX) -> {
                    telegramBotService.checkAnswer(json, callbackData, trainer, chatId)
                    telegramBotService.checkNextQuestionAndSend(json, trainer, chatId)
                }

            }
        }
    }
}