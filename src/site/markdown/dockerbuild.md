# Docker: Building the Docker Container

## Build the Docker Container (locally)

To build a local version of the container (for testing) and running it, execute following commands.

### Clone the Repository

Clone this repository, if not done yet:

```
git clone https://github.com/finmath/finmath-smart-derivative-contract.git
cd finmath-smart-derivative-contract
```

### Building the Docker Container using Spring Boot

*Note:* provide users and passwords via an application.yml file that resides in `/PATH/TO/YOUR/CONFIG` (on the machine
running Docker).

```
mvn spring-boot:build-image
docker run -v /PATH/TO/YOUR/CONFIG:/workspace/config -p 8080:8080 docker.io/finmath/finmath-smart-derivative-contract:0.1.9-SNAPSHOT
```

Remark: The app will run under `workspace`, hence we mount `/workspace/config`. If the working directory changed, this
has to be adapted.

### Building a Docker Container using the Docker File

*Note:* provide users and passwords via an application.yml file that resides in `/PATH/TO/YOUR/CONFIG` (on the machine
running Docker).

```
docker build -t valuation_service .
docker run -v /PATH/TO/YOUR/CONFIG:/config -p 8080:8080 valuation_service
```

## Build the Docker Container (creating an image and pushing it under the finmath user)

```
mvn spring-boot:build-image
docker login
docker push finmath/finmath-smart-derivative-contract:${project.version}
```

With `${project.version}` being the version of the artifact.
