FROM eclipse-temurin:21-jdk
WORKDIR /app
CMD ["./gradlew", "clean", "shadowJar"]
COPY build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]