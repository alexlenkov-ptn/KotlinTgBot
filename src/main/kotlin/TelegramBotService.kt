import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val HOST_API_TELEGRAM = "https://api.telegram.org"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

class TelegramBotService(private val botToken: String) {

    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$HOST_API_TELEGRAM/bot$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(chatId: Int?, text: String?): String? {
        val encodedText = URLEncoder.encode(
            text,
            StandardCharsets.UTF_8.toString()
        )
        val urlSendMessage = "$HOST_API_TELEGRAM/bot$botToken/sendMessage?chat_id=$chatId&text=$encodedText"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }


    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, chatId: Int?) {
        if (trainer.getNextQuestion() == null) {
            this.sendMessage(
                chatId,
                "Все слова выучены"
            )

        } else {
            this.sendQuestionWords(chatId, trainer.getNextQuestion())
        }
    }


    fun sendMenu(chatId: Int?): String? {
        val sendMenuBody = """
            {
            	"chat_id": $chatId,
            	"text": "Выберите действие",
            	"reply_markup": {
            		"inline_keyboard": [
            			[
            				{
            					"text": "Изучить слова",
            					"callback_data": "learn_words_clicked"
            				},
            				{
            					"text": "Статистика",
            					"callback_data": "statistics_clicked"
            				}
            			]
            		]
            	}
            }
        """.trimIndent()

        val urlSendMessage = "$HOST_API_TELEGRAM/bot$botToken/sendMessage"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    private fun sendQuestionWords(chatId: Int?, question: Question?): String? {
        val text = question?.correctAnswer?.original

        val variants = question?.variants?.mapIndexed { index, word ->
            """
                {
                    "text": "${word.translate}",
                    "callback_data": "$CALLBACK_DATA_ANSWER_PREFIX$index"
                }
            """.trimIndent()
        }?.joinToString(",")

        val sendQuestionWords = """
            {
                "chat_id": $chatId,
                "text": "Укажите перевод для слова: <$text>",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                           $variants
                        ]
                    ]
                }
            }
        """.trimIndent()

        val urlSendMessage = "$HOST_API_TELEGRAM/bot$botToken/sendMessage"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendQuestionWords))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun checkAnswer(callbackData: String, trainer: LearnWordsTrainer, chatId: Int?) {

        if (callbackData.startsWith(CALLBACK_DATA_ANSWER_PREFIX)) {
            val indexAnswer = callbackData.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()

            when(trainer.checkAnswer(indexAnswer)) {
                true -> this.sendMessage(chatId,
                    "Правильно!")
                false -> this.sendMessage(chatId,
                    "Неправильно! Корректный ответ: ${trainer.question?.correctAnswer?.translate}")

            }
        }
    }
}