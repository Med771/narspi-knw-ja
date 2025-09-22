# Этап 1: Сборка
FROM eclipse-temurin:21-jdk AS builder

# Установка рабочей директории
WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Сборка приложения с помощью Maven
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests

# Этап 2: Запуск
FROM eclipse-temurin:21-jre

# Создаём рабочую директорию в контейнере
WORKDIR /app

# Копируем собранный .jar файл из предыдущего этапа
COPY --from=builder /app/target/*.jar app.jar

# Указываем команду запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
