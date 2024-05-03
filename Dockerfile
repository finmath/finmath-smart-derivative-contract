# Build Stage: Build fat-jar
FROM maven:3.8.1-openjdk-17 as BuildStage
COPY .git /usr/src/app/.git
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package spring-boot:repackage

# Run Stage: Copy fat-jar and define Entrypoint
FROM openjdk:17 as RunStage
COPY --from=BuildStage /usr/src/app/target/finmath-smart-derivative-contract-1.0.7-SNAPSHOT.jar /usr/app/finmath-smart-derivative-contract-1.0.7-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/usr/app/finmath-smart-derivative-contract-1.0.7-SNAPSHOT.jar"]
