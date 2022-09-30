FROM openjdk:17

COPY target/finmath-smart-derivative-contract-0.1.8-SNAPSHOT.jar .

ENV SDC_HOME src/main/deploy

ENTRYPOINT ["java", "-jar", "finmath-smart-derivative-contract-0.1.8-SNAPSHOT.jar", "sdc"]