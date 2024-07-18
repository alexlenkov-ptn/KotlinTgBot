import java.io.File

data class Word(
    val original : String,
    val translate : String,
    val correctAnswersCount : Int = 0,
)

fun main() {
    val wordsFile: File = File("words.txt")
    val dictionary: MutableList<Word> = mutableListOf()

    for (string in wordsFile.readLines()) {
        val split = string.split("|")
        try {
            val word = Word(original = split[0],
                translate = split[1],
                correctAnswersCount = split[2].toInt())
            dictionary.add(word)
        }catch (e: Exception) {
            val word = Word(original = split[0],
                translate = split[1])
            dictionary.add(word)
        }
    }

    dictionary.map { println(it) }

}