@file:Suppress("UNUSED_CHANGED_VALUE")

import java.io.File

const val CORRECT_ANSWER = 3
const val ANSWER_OPTIONS = 4
const val ONE_WORD = 1

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
        val userInput = readln().toIntOrNull()
        when (userInput) {
            1 -> {
                println("Учим слова")
                dictionary.printWords()
                break
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

fun MutableList<Word>.printStatistics(): String {
    val allElements = this.count()

    val correctAnswer = this.count { it.correctAnswersCount >= CORRECT_ANSWER }
    val percentResult = ((correctAnswer.toDouble() / allElements.toDouble()) * 100).toInt()
    return "Выучено $correctAnswer из $allElements слов | $percentResult%"
}

fun MutableList<Word>.printWords() {
    while (true) {
        var unlearnedWords: MutableList<Word> = this.filter { it.correctAnswersCount < CORRECT_ANSWER }.toMutableList()


        if (unlearnedWords.isEmpty()) {
            println("Все слова выучены")
            break
        }

        while (unlearnedWords.count() < ANSWER_OPTIONS) {
            unlearnedWords.add(this.filter { it.correctAnswersCount >= CORRECT_ANSWER }.random())
        }
        // todo: Теперь сюда приходят не уникальные слова. Нужно сделать их уникальными

        val unlearnedWordsOptions = unlearnedWords.shuffled().take(ANSWER_OPTIONS)
        println(
            "Загадываемое слово ${unlearnedWordsOptions.random().original} \n" +
                    "Варианты ответа:"
        )

        unlearnedWordsOptions.mapIndexed { index, word -> println("${index + 1}.${word.translate}") }


        println("Напишите ответ: ")
        val userInput = readln().toIntOrNull()
    }
}