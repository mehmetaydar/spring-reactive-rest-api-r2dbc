FROM maven:3.8.3-openjdk-17 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM eclipse-temurin:17-alpine
COPY --from=build /usr/src/app/target/user-management-spring-reactive-0.0.1-SNAPSHOT.jar /usr/app/app.jar
ENV SPRING_PROFILES_ACTIVE=test
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=70",  "-jar", "/usr/app/app.jar"]
