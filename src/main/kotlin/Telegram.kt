import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
    val trainers = HashMap<Long, LearnWordsTrainer>()

    while (true) {
        Thread.sleep(2000)

        val telegramBotService = TelegramBotService(botToken)

        val responseString: String = telegramBotService.getUpdates(lastUpdateId)
        val response: Response = telegramBotService.responseDecodeFromString(responseString)

        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, telegramBotService, trainers) }
        lastUpdateId = sortedUpdates.last().updateId + 1

    }
}

fun handleUpdate(
    firstUpdate: Update,
    telegramBotService: TelegramBotService,
    trainer: HashMap<Long, LearnWordsTrainer>
) {

    val userMessage = firstUpdate.message?.text
    val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id ?: return
    val callbackData = firstUpdate.callbackQuery?.data

    val trainerForUser = trainer.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }


    if (callbackData != null || userMessage != null) {
        when {
            userMessage?.lowercase() == Constants.STRING_START -> {
                telegramBotService.sendMenu(chatId)
            }

            userMessage?.lowercase() == Constants.STRING_MENU -> {
                telegramBotService.sendMenu(chatId)
            }

            callbackData?.lowercase() == Constants.CALLBACK_LEARN_WORDS_CLICKED -> {
                telegramBotService.checkNextQuestionAndSend(trainerForUser, chatId)
            }

            callbackData?.lowercase() == Constants.CALLBACK_STATISTICS_CLICKED -> {
                telegramBotService.sendMessage(
                    chatId,
                    "Выучено ${trainerForUser.getStatistics().correctAnswer} из " +
                            "${trainerForUser.getStatistics().allElements} слов | " +
                            "${trainerForUser.getStatistics().percentResult}%"
                )
            }

            callbackData?.lowercase() == Constants.CALLBACK_RESET_CLICKED -> {
                trainerForUser.resetProgress()
                telegramBotService.sendMessage(
                    chatId,
                    "Прогресс сброшен"
                )
                telegramBotService.sendMenu(chatId)
            }

            callbackData?.startsWith(Constants.CALLBACK_DATA_ANSWER_PREFIX) == true -> {
                telegramBotService.checkAnswer(callbackData, trainerForUser, chatId)
                telegramBotService.checkNextQuestionAndSend(trainerForUser, chatId)
            }

            callbackData?.startsWith(Constants.CALLBACK_EXIT_MENU) == true -> {
                telegramBotService.sendMenu(chatId)
            }

        }
    }
}
