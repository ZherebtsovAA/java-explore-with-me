FROM amazoncorretto:11-alpine-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} MainServiceApp.jar
ENTRYPOINT ["java", "-jar", "/MainServiceApp.jar"]