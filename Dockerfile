# ដំណាក់កាលទី១៖ Build Project ជាមួយ Java 21 (JDK) លើ Render Cloud
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy ឯកសារសម្រាប់ Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# បើកសិទ្ធិឱ្យ gradlew ដំណើរការក្នុង Linux container
RUN chmod +x gradlew

# Run command ដើម្បី Build jar file លើ Cloud
RUN ./gradlew bootJar --no-daemon

# ដំណាក់កាលទី២៖ Run លើ Java 21 Runtime (JRE ស្រាល)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar file ដែលបាន build រួចពីដំណាក់កាលទី១ មកដាក់ទីនេះ
COPY --from=build /app/build/libs/*.jar app.jar

# បើក Port 8080
EXPOSE 8080

# ដំណើរការកម្មវិធី
ENTRYPOINT ["java", "-jar", "app.jar"]
