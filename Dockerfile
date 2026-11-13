FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 👈 កំណត់ឈ្មោះ Jar file ឱ្យចំជាក់លាក់តែម្តង ដើម្បីកុំឱ្យមានបញ្ហា
COPY build/libs/student-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]