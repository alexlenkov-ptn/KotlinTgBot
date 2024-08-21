@file:Suppress("UNUSED_CHANGED_VALUE")

const val INT_MAX_CORRECT_ANSWER = 3
const val INT_ANSWER_VARIANTS = 4
const val STRING_CORRECT_ANSWER = "Ответ правильный"
const val STRING_INCORRECT_ANSWER = "Ответ неправильный"
const val INT_ZERO = 0
const val INT_ONE = 1

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index, word -> "${index + 1}.${word.translate}" }
        .joinToString(separator = "\n")
    return this.correctAnswer.original + "\n" + variants + "\n0.Выйти в меню"
}

fun main() {

    val trainer = LearnWordsTrainer()
    val dictionary = trainer.dictionary

    while (true) {
        println(
            "Меню: \n" +
                    "1 – Учить слова\n" +
                    "2 – Статистика\n" +
                    "0 – Выход"
        )
        val userInput = readln().toIntOrNull()
        when (userInput) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()

                    if (question == null) {
                        println("Все слова выучены")
                        break
                    }

                    println(question.asConsoleString())

                    var userInput = readln().toIntOrNull()

                    if (userInput == INT_ZERO) {
                        break
                    }

                    while (userInput == null) {
                        println("Вы не ввели число. Введите число:")
                        userInput = readln().toIntOrNull()
                    }

                    if (trainer.checkAnswer(userInput.minus(INT_ONE))) {
                        println(STRING_CORRECT_ANSWER)
                    } else {
                        println(STRING_INCORRECT_ANSWER)
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println(
                    "Выучено ${statistics.correctAnswer} из ${statistics.allElements} слов | ${statistics.percentResult}%"
                )
                break
            }

            0 -> {
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





