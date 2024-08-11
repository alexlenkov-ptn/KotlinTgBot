@file:Suppress("UNUSED_CHANGED_VALUE")

import java.io.File

const val INT_CORRECT_ANSWER = 3
const val INT_ANSWER_OPTIONS = 4
const val STRING_CORRECT_ANSWER = "Ответ правильный"
const val STRING_INCORRECT_ANSWER = "Ответ неправильный"
const val INT_NULL = 0
const val INT_ONE = 1
const val INT_MINUS_ONE = -1


data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun main() {
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
                println("Учим слова")
                dictionary.printWords()
            }

            2 -> {
                println(
                    "Смотрим статистику\n" +
                            dictionary.printStatistics()
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

fun List<Word>.printStatistics(): String {
    val allElements = this.count()

    val correctAnswer = this.count { it.correctAnswersCount >= INT_CORRECT_ANSWER }
    val percentResult = ((correctAnswer.toDouble() / allElements.toDouble()) * 100).toInt()
    return "Выучено $correctAnswer из $allElements слов | $percentResult%"
}

fun List<Word>.printWords() {
    while (true) {
        val unlearnedWords: MutableList<Word> =
            this.filter { it.correctAnswersCount < INT_CORRECT_ANSWER }.toMutableList()

        if (unlearnedWords.isEmpty()) {
            println("Все слова выучены")
            break
        }

        while (unlearnedWords.count() < INT_ANSWER_OPTIONS) {
            val randomWord = this.filter { it.correctAnswersCount == INT_CORRECT_ANSWER }.random()
            while (!unlearnedWords.contains(randomWord)) {
                unlearnedWords.add(randomWord)
            }
        }

        val unlearnedWordsOptions = unlearnedWords.shuffled().take(INT_ANSWER_OPTIONS)
        val secretWord = unlearnedWordsOptions.filter { it.correctAnswersCount < INT_CORRECT_ANSWER }.random().original

        println(
            "Загадываемое слово: <$secretWord> \n" +
                    "Варианты ответа:"
        )

        unlearnedWordsOptions.mapIndexed { index, word -> println("${index + 1}.${word.translate}") }


        println(
            "${INT_NULL}.Выйти в главное меню\n" +
                    "Напишите ответ: "
        )

        val userInput = readln().toIntOrNull() ?: INT_MINUS_ONE

        if (userInput == INT_NULL) {
            break
        }

        val answerUserWord = unlearnedWordsOptions.getOrNull(userInput.minus(INT_ONE))

        if (secretWord == answerUserWord?.original) {
            answerUserWord.correctAnswersCount++
            println(STRING_CORRECT_ANSWER)
            saveDictionary(File("words.txt"), this)
        } else {
            println(STRING_INCORRECT_ANSWER)
        }
    }
}

fun saveDictionary(file: File, dictionary: List<Word>) {
    file.writeText("")
    dictionary.map {
        file.appendText(
            "${it.original}|${it.translate}|${it.correctAnswersCount}\n"
        )
    }
}