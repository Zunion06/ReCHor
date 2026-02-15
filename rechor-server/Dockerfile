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

COPY --from=build /app/rechor-server/target/*.jar app.jar

RUN mkdir -p /app/data/
COPY rechor-server/src/main/resources/ /app/data/
RUN mv data/timetable-mini data/timetable

RUN useradd -m noaccess
USER noaccess

ENTRYPOINT ["sh", "-c", "java -DRECHOR_MINI=true -Xmx512m -Xms512m -Drechor.data.dir=/app/data -jar app.jar"]