FROM maven:3-jdk-8-alpine AS build

WORKDIR /opt/app

COPY ./ /opt/app
RUN mvn clean install -DskipTests

FROM openjdk:8-jdk-alpine

COPY --from=build /opt/app/target/*.jar app.jar
ENV PORT 9999
EXPOSE $PORT

ENTRYPOINT ["java", "-jar", "-Xmx1024M", "-Dserver.port=${PORT}", "app.jar"]