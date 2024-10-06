# English Learning Bot

Бот для изучения английских слов. Слова размещаются в 
файле words.txt, в формате: `английское слово|перевод|0`.
Каждая строка соответствует изучаемому слову. 
При запуске бота новым пользователем, файл words.txt копируется с именем 
`id_чата_пользователя.txt`.

## Публикация

Для публикации бота на VPS воспользуемся утилитой scp, для запуска – ssh.

### Настройка VPS

1. Создать виртуальный сервер (Ubuntu), получить: ip-адрес, пароль для root пользователя
2. Подключиться к серверу по SSH используя команду `ssh root@80.78.243.202` и введя пароль
3. Обновить установленные пакеты командами `apt update` и `apt upgrade`
4. Устанавливаем JDK коммандой `apt install default-jdk`
5. Убедиться что JDK установлена командой `java --version`

### Публикация и запуск

1. Соберем shadowJar командой `./gradlew shadowJar`
2. Копируем jar на наш VPS переименуя его одновременно в bot.jar: `scp build/libs/KotlinTgBot-1.0-SNAPSHOT-all.jar root@80.78.243.202:/root/bot.jar`
3. Копируем words.txt на VPS: `scp words.txt root@80.78.243.202:/root/words.txt`
4. Подключиться к серверу по SSH используя команду ssh root@100.100.100.100 и введя пароль
5. Запустить бота в фоне командой `nohup java -jar bot.jar <ТОКЕН ТЕЛЕГРАМ> &`
6. Проверить работу бота

## Принципы

- KISS
- DRY
- Единство ответственности
- Избегание преждевременной оптимизации