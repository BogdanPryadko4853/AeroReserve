FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем Docker CLI
RUN apk add --no-cache docker-cli

WORKDIR /app

# Копируем JAR файл
COPY target/*.jar app.jar

# Создаем директории для бэкапов
RUN mkdir -p /backups /backup-scripts

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]