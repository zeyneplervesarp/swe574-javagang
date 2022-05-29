FROM maven:3.6.3-amazoncorretto-11 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
COPY lib /usr/src/app/lib
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

FROM amazoncorretto:11-alpine
COPY --from=build /usr/src/app/target/socialhub-api-0.0.1-SNAPSHOT.jar /usr/app/app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]