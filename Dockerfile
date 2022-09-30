FROM openjdk:17

COPY src/main/deploy/etc/sdc.properties src/main/deploy/etc/sdc.properties
COPY target/finmath-smart-derivative-contract-0.1.8-SNAPSHOT.jar .

ENV SDC_HOME src/main/deploy

ENTRYPOINT ["java", "-jar", "finmath-smart-derivative-contract-0.1.8-SNAPSHOT.jar", "sdc"]