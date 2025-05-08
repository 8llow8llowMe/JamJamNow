FROM ibm-semeru-runtimes:open-21-jre-jammy

ARG JAR_FILE=./build/libs/*.jar

COPY ${JAR_FILE} /app/api-service.jar

ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=$ACTIVE_PROFILE -jar /app/api-service.jar"]