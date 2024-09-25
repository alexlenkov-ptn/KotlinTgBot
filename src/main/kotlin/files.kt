fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index, word -> "${index + 1}.${word.translate}" }
        .joinToString(separator = "\n")
    return this.correctAnswer.original + "\n" + variants + "\n0.Выйти в меню"
}

fun main() {

    val trainer = LearnWordsTrainer()

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
                        println("Правильно!")
                    } else {
                        println("Неправильно! Правильный перевод: <${question.correctAnswer.translate}>")
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println(
                    "Выучено ${statistics.correctAnswer} из ${statistics.allElements} слов | ${statistics.percentResult}%"
                )
                continue
            }

            0 -> {
                println("Выходим")
                break
            }

            else -> {
                println("Нажмите 1, 2 или 0")
                continue
            }
        }
    }
}





