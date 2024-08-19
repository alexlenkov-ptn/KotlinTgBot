@file:Suppress("UNUSED_CHANGED_VALUE")

import java.io.File

const val INT_MAX_CORRECT_ANSWER = 3
const val INT_ANSWER_VARIANTS = 4
const val STRING_CORRECT_ANSWER = "Ответ правильный"
const val STRING_INCORRECT_ANSWER = "Ответ неправильный"
const val INT_ZERO = 0
const val INT_ONE = 1
const val INT_MINUS_ONE = -1


data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

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
                        return break
                    }

                    println(
                        "Загадываемое слово: <${question.correctAnswer.original}> \n" +
                                "Варианты ответа:"
                    )
                    question.variants.mapIndexed { index, word -> println("${index + 1}.${word.translate}") }
                    println(
                        "${INT_ZERO}.Выйти в главное меню\n" +
                                "Напишите ответ: "
                    )
                    var userInput = readln().toIntOrNull()

                    trainer.checkAnswer(userInput?.minus(INT_ONE))


                    if (userInput == INT_ZERO) {
                        break
                    }
                    while (userInput == null) {
                        println("Введите число")
                        userInput = readln().toIntOrNull() ?: null
                    }


                    val answerUserWord = question.variants.getOrNull(userInput.minus(INT_ONE))


                    if (question.correctAnswer == answerUserWord) {

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





