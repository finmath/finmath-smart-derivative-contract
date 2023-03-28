# Finmath Smart Derivative Contracts

****************************************

**Algorithms and methodologies related to smart derivative contracts.**

****************************************

## Requirements
- Java 17+ (we recommend the Eclipse Adoptium Java release);
- Maven 3.8+;
- _(optional, Maven can download it for you*)_ Node.js v18+ LTS;

_* unless you don't want to or you are behind some pesky firewall._

## Contents of this Repo

### Contents and manual startup

The **sdc-frontend** module contains the frontend application to interact with the SDC infastructure. You can build it and test it like this:
- go to https://nodejs.org/en/download and check what's the latest LTS release from the v18 line;
- bump the Node version string in `sdc-frontend/pom.xml`;
- open a terminal;
- move into the **sdc-frontend** root folder;
- if you are managing your own install of Node, run 

~~~
 mvn versions:display-property-update -DiHaveNode=true
~~~

and apply updates if necessary with

~~~
  mvn versions:update-properties -DiHaveNode=true
~~~

then run

~~~
  mvn clean install -DiHaveNode=true
~~~

- if you want Maven to install a minimal Node environment instead, then run

~~~
  mvn versions:display-property-update -DiNeedNode=true
~~~

Apply updates if necessary with 

~~~
  mvn versions:update-properties -DiNeedNode=true
~~~

and then run 

~~~
  mvn clean install -DiNeedNode=true
~~~

- follow the instructions that appear on screen, if any. Then start the app using `npm run start`;
- enjoy our work!

The **sdc-service** module contains the Java part of this project. There you can find:
- an offline demo that briefly explains how SDC works;
- the full backend suite (except for the blockchain-specific stuff), including the SDC Valuation Oracle.

To update the **sdc-service** project deps:
- go to https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-dependencies and check what's the latest version;
- bump the _spring-boot-dependencies_ version string in `sdc-service/pom.xml`;
- open a terminal;
- move into the **sdc-service** root folder;
- Check for updates with 
~~~
  mvn versions:display-property-update
~~~
and apply updates if necessary with 
~~~
  mvn versions:update-properties
~~~
- Install the project with 
~~~
  mvn clean install
~~~

To run the offline demo, you have to:
- open a terminal;
- move into the **sdc-service** root folder;
- run 
~~~
  mvn exec:java -Dexec.mainClass=net.finmath.smartcontract.demo.VisualiserSDC
~~~

To start the backend service, you have to:
- open a terminal;
- move into the **sdc-service** root folder;
- run 
~~~
  mvn spring-boot:run
~~~

### Automatic startup

- You may perform some of the above tasks with convenient scripts provided in the `scripts` folder _[**TODO**: update bash scripts and write PowerShell scripts for windows (batch scripts are kinda dangerous)]_ 

## License

The code is distributed under the [Apache License version 2.0][], unless otherwise explicitly stated.

[Apache License version 2.0]: http://www.apache.org/licenses/LICENSE-2.0.html


