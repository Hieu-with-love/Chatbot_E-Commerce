FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} chatbot_spring.jar

ENTRYPOINT ["java", "-jar", "chatbot_spring.jar"]

EXPOSE 9080