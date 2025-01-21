FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvn pom.xml ./
RUN ./mvn dependency:go-offline
COPY ./src ./src
RUN ./mvn clean install

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/target/hw17-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]