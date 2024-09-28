FROM eclipse-temurin:21-jdk-alpine

EXPOSE 8080
ADD target/*.jar /opt/api.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /opt/api.jar

