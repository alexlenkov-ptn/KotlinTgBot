import kotlinx.serialization.SerialName
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.xml.crypto.Data

fun main() {

    val json = Json {
        ignoreUnknownKeys = true
    }

    val responseString = """
        {
        	"ok": true,
        	"result": [
        		{
        			"update_id": 55747971,
        			"message": {
        				"message_id": 24,
        				"from": {
        					"id": 398487011,
        					"is_bot": false,
        					"first_name": "Alexander",
        					"last_name": "Lenkov",
        					"username": "mediabiker",
        					"language_code": "ru",
        					"is_premium": true
        				},
        				"chat": {
        					"id": -4584886098,
        					"title": "groupbot",
        					"type": "group",
        					"all_members_are_administrators": true
        				},
        				"date": 1727252503,
        				"text": "/start",
        				"entities": [
        					{
        						"offset": 0,
        						"length": 6,
        						"type": "bot_command"
        					}
        				]
        			}
        		}
        	]
        }
    """.trimIndent()

//    val word = Json.encodeToString(
//        Word(
//        original = "Hello",
//        translate = "Привет",
//        2,
//    ))
//
//    println(word)
//
//    val wordObject = Json.decodeFromString<Word>(
//        """
//            {"original":"Hello","translate":"Привет","correctAnswersCount":2}
//            """
//    )
//
//    println(wordObject)


    val response = json.decodeFromString<Response>(responseString)
    println(response)

}