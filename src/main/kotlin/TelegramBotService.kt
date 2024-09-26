import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long? = null,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

class TelegramBotService(private val botToken: String) {

    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "${Constants.HOST_API_TELEGRAM}/bot$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(json: Json, chatId: Long, text: String): String? {

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = text,
        )

        val requestBodyString = json.encodeToString(requestBody)

        val urlSendMessage = "${Constants.HOST_API_TELEGRAM}/bot$botToken/sendMessage"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }


    fun checkNextQuestionAndSend(json: Json, trainer: LearnWordsTrainer, chatId: Long) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            this.sendMessage(
                json,
                chatId,
                "Все слова выучены"
            )
        } else {
            this.sendQuestionWords(json, chatId, question)
        }
    }


    fun sendMenu(json: Json, chatId: Long): String? {

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = Constants.STRING_SELECT_ACTION,
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(
                            text = Constants.STRING_LEARN_WORDS,
                            callbackData = Constants.CALLBACK_LEARN_WORDS_CLICKED,
                        ),
                        InlineKeyboard(
                            text = Constants.STRING_STATISTICS,
                            callbackData = Constants.CALLBACK_STATISTICS_CLICKED,
                        ),
                    )
                )
            )
        )

        val requestBodyString = json.encodeToString(requestBody)


        val urlSendMessage = "${Constants.HOST_API_TELEGRAM}/bot$botToken/sendMessage"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    private fun sendQuestionWords(json: Json, chatId: Long, question: Question): String? {

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.correctAnswer.original,
            replyMarkup = ReplyMarkup(
                listOf(
                    question.variants.mapIndexed { index, word ->
                        InlineKeyboard(
                            text = word.translate,
                            callbackData = "${Constants.CALLBACK_DATA_ANSWER_PREFIX}$index",
                        )
                    }
                )
            )
        )

        val requestBodyString = json.encodeToString(requestBody)

        val urlSendMessage = "${Constants.HOST_API_TELEGRAM}/bot$botToken/sendMessage"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun checkAnswer(json: Json, callbackData: String, trainer: LearnWordsTrainer, chatId: Long) {

        if (callbackData.startsWith(Constants.CALLBACK_DATA_ANSWER_PREFIX)) {
            val indexAnswer = callbackData.substringAfter(Constants.CALLBACK_DATA_ANSWER_PREFIX).toInt()

            when (trainer.checkAnswer(indexAnswer)) {
                true -> this.sendMessage(
                    json,
                    chatId,
                    "Правильно!"
                )

                false -> this.sendMessage(
                    json,
                    chatId,
                    "Неправильно! Корректный ответ: ${trainer.question?.correctAnswer?.translate}"
                )

            }
        }
    }
}