import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val HOST_API_TELEGRAM = "https://api.telegram.org"

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        val updateIdRegex : Regex = "\"update_id\":(.+?),".toRegex()
        val matchResult : MatchResult? = updateIdRegex.find(updates)
        val groups = matchResult?.groups

        updateId = (groups?.get(1)?.value?.toInt()?.plus(1)) ?: 0
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "$HOST_API_TELEGRAM/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}