FROM amazoncorretto:11-alpine-jdk
ARG JAR_FILE=stats/target/*.jar
COPY ${JAR_FILE} StatsServiceApp.jar
ENTRYPOINT ["java", "-jar", "/StatsServiceApp.jar"]