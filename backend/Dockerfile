FROM eclipse-temurin:21-alpine AS runtime

# metadate
LABEL maintainer="notetakingforeggs@gmail.com"
LABEL org.opencontainers.image.source="https://github.com/notetakingforeggs/whitechapel_advice"

# copy JAR into the image
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]