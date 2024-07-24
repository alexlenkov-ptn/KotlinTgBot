fun main() {

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
                println("Смотрим статистику")
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