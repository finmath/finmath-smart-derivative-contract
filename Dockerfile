# Build Stage: Build fat-jar
FROM maven:3.8.1-openjdk-17 as BuildStage
COPY .git /usr/src/app/.git
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package spring-boot:repackage -DskipTests

# Run Stage: Copy fat-jar and define Entrypoint
#
# Important: You have to set the version that should be used here, e.g.
# The version is is currently build by the above script:
# finmath-smart-derivative-contract-1.2.11-SNAPSHOT
# The version that is on maven central
# finmath-smart-derivative-contract-1.2.10
#
FROM openjdk:17 as RunStage
COPY --from=BuildStage /usr/src/app/target/finmath-smart-derivative-contract-1.2.12.jar /usr/app/finmath-smart-derivative-contract-1.2.12.jar
ENTRYPOINT ["java", "-jar", "/usr/app/finmath-smart-derivative-contract-1.2.12.jar"]
