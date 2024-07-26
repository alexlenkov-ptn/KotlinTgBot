import java.io.File
import kotlin.math.round

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0,
)

fun main() {
    val wordsFile: File = File("words.txt")
    val dictionary: MutableList<Word> = mutableListOf()

    for (string in wordsFile.readLines()) {
        val split = string.split("|")
        val word = Word(
            original = split[0],
            translate = split[1],
            correctAnswersCount = split.getOrNull(2)?.toIntOrNull() ?: 0
        )
        dictionary.add(word)
    }
    while (true) {
        println(
            "Меню: \n" +
                    "1 – Учить слова\n" +
                    "2 – Статистика\n" +
                    "0 – Выход"
        )
        val userInput = readln().toString()
        when (userInput) {
            "1" -> {
                println("Учим слова")
                break
            }

            "2" -> {
                println(
                    "Смотрим статистику\n" +
                            dictionary.filter()
                )
                break
            }

            "0" -> {
                println("Выходим")
                break
            }

            else -> {
                println("Нажмите 1, 2 или 3")
                continue
            }
        }
    }
}

fun MutableList<Word>.filter(): String {
    val allElements = this.count()
    var correctAnswer = 0
    this.map {
        if (it.correctAnswersCount >= 3) correctAnswer++
    }
    val percentResult = ((correctAnswer.toDouble() / allElements.toDouble()) * 100).toInt()
    return "Выучено $correctAnswer из $allElements слов | $percentResult%"
}