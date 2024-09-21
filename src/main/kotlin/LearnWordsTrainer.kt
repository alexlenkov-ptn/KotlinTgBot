import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

class Statistics(
    val allElements: Int,
    val correctAnswer: Int,
    val percentResult: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val learnedAnswerCount: Int = 3,
    private val countOfQuestionWords: Int = 4,
) {

    private var question: Question? = null

    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val allElements = dictionary.count()
        val correctAnswer = dictionary.count { it.correctAnswersCount >= learnedAnswerCount }
        val percentResult = ((correctAnswer.toDouble() / allElements.toDouble()) * 100).toInt()
        return Statistics(allElements, correctAnswer, percentResult)
    }

    fun getNextQuestion(): Question? {

        val unlearnedWordsList: List<Word> =
            dictionary.filter { it.correctAnswersCount < learnedAnswerCount }.toMutableList()

        if (unlearnedWordsList.isEmpty()) return null

        val allUnlearnedWords = unlearnedWordsList.toMutableList()

        if (allUnlearnedWords.count() < countOfQuestionWords) {
            val missingAnswerCount = countOfQuestionWords.minus(allUnlearnedWords.count())
            val missingAnswerList: List<Word> =
                dictionary.shuffled().filter { it.correctAnswersCount >= learnedAnswerCount }
                    .take(missingAnswerCount)
            allUnlearnedWords.addAll(missingAnswerList)
        }

        val variants = allUnlearnedWords.shuffled().take(countOfQuestionWords)
        val correctAnswer = variants.filter { it.correctAnswersCount < learnedAnswerCount }.random()

        question = Question(
            variants,
            correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(File("words.txt"), dictionary)
                return true
            } else {
                return false
            }
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        val wordsFile: File = File("words.txt")
        try {
            val dictionary: List<Word> = wordsFile.readLines().map {
                val split = it.split("|")
                Word(
                    original = split[0],
                    translate = split[1],
                    correctAnswersCount = split.getOrNull(2)?.toIntOrNull() ?: 0
                )
            }
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("некорректный файл")
        }
    }
}

private fun saveDictionary(file: File, dictionary: List<Word>) {
    file.writeText("")
    dictionary.map {
        file.appendText(
            "${it.original}|${it.translate}|${it.correctAnswersCount}\n"
        )
    }
}

