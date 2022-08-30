API тесты для сервиса https://test-api-market.herokuapp.com/swagger-ui/index.html#

Стек: Kotlin + Gradle + Fuel + Jackson + Junit5 + Allure

Запуск тестов:
1. Склонировать репозиторий - git clone git@github.com:we1rd1k/BlancBankApiTests.git
2. Перейти в директорию с проектом
3. Запустить тесты промаркерованные тегом API: ./gradlew clean runApiTestSet

Для получения отчета в формате Allure выполнить ./gradlew allureServe
