# ------------ BUILD ------------ 
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app


COPY pom.xml .
COPY rechor-model/pom.xml rechor-model/
COPY rechor-logic/pom.xml rechor-logic/
COPY rechor-server/pom.xml rechor-server/
COPY rechor-desktop/pom.xml rechor-desktop/

# This download dependencies 
RUN mvn dependency:go-offline -B

COPY rechor-model/src rechor-model/src
COPY rechor-logic/src rechor-logic/src
COPY rechor-server/src rechor-server/src

RUN mvn clean package -pl rechor-server -am -DskipTests

# ------------ RUN ------------ 
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# For security
# If attacker gains access to the container, it won't be as a root user
# Therefore the attacker can't download or remove anything
RUN useradd -m rechoruser
USER rechoruser

COPY --from=build /app/rechor-server/target/*.jar app.jar

# Using "sh -c" allows the shell to expand the $JAVA_OPTS variable
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]