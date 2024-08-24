import java.io.File

class Statistics(
    val allElements: Int,
    val correctAnswer: Int,
    val percentResult: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    private var question: Question? = null

    val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val allElements = dictionary.count()
        val correctAnswer = dictionary.count { it.correctAnswersCount >= INT_MAX_CORRECT_ANSWER }
        val percentResult = ((correctAnswer.toDouble() / allElements.toDouble()) * 100).toInt()
        return Statistics(allElements, correctAnswer, percentResult)

    }

    fun getNextQuestion(): Question? {

        val unlearnedWordsList: List<Word> =
            dictionary.filter { it.correctAnswersCount < INT_MAX_CORRECT_ANSWER }.toMutableList()

        if (unlearnedWordsList.isEmpty()) return null

        val allUnlearnedWords = unlearnedWordsList.toMutableList()

        if (allUnlearnedWords.count() < INT_ANSWER_VARIANTS) {
            val missingAnswerCount = INT_ANSWER_VARIANTS.minus(allUnlearnedWords.count())
            val missingAnswerList: List<Word> =
                dictionary.shuffled().filter { it.correctAnswersCount == INT_MAX_CORRECT_ANSWER }
                    .take(missingAnswerCount)
            allUnlearnedWords.addAll(missingAnswerList)
        }

        val variants = allUnlearnedWords.shuffled().take(INT_ANSWER_VARIANTS)
        val correctAnswer = variants.filter { it.correctAnswersCount < INT_MAX_CORRECT_ANSWER }.random()

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
        val dictionary: List<Word> = wordsFile.readLines().mapNotNull {
            val split = it.split("|")
            if (split.size >= 3) {
                Word(
                    original = split[0],
                    translate = split[1],
                    correctAnswersCount = split.getOrNull(2)?.toIntOrNull() ?: 0
                )
            } else {
                null
            }
        }
        return dictionary
    }

    private fun saveDictionary(file: File, dictionary: List<Word>) {
        file.writeText("")
        dictionary.map {
            file.appendText(
                "${it.original}|${it.translate}|${it.correctAnswersCount}\n"
            )
        }
    }
}

