#FROM ubuntu:latest
#LABEL authors="ASUS"
#
#ENTRYPOINT ["top", "-b"]

# ដំណាក់កាលទី១៖ Build Project ជាមួយ Java 21 (JDK)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy ឯកសារសម្រាប់ Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 👈 បន្ថែមបន្ទាត់នេះដើម្បីបើកសិទ្ធិឱ្យ gradlew ក្នុង Linux container ផ្ទាល់
RUN chmod +x gradlew

# Run command ដើម្បី Build jar file
RUN ./gradlew bootJar --no-daemon

# ដំណាក់កាលទី២៖ Run លើ Java 21 Runtime (JRE ស្រាល)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar file ពីដំណាក់កាលទី១
COPY --from=build /app/build/libs/*.jar app.jar

# បើក Port 8080
EXPOSE 8080

# ដំណើរការแอปพลิเคชัน
ENTRYPOINT ["java", "-jar", "app.jar"]