FROM openjdk:8-jre-slim
EXPOSE 8080
ARG JAR_FILE=target/test-gcp-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]