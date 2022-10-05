# finmath smart derivative contracts

****************************************

**Algorithms and methodologies related to smart derivative contracts.**

****************************************

## Introduction

The finmath smart-derivative-contract libraries provides (JVM) implementations of methodologies related to smart
derivative contracts.

### Literature

For a technical/mathematical description of the concept of a smart derivative contract
see https://ssrn.com/abstract=3163074

For non-technical description see the [articles](articles) section.

## Contents

### Visualization and Demonstration

The package `net.finmath.smartcontract.demo`contains a visualization of a smart derivative contract.
This is a Java FX application. Run `VisualiserSDC`.

### Valuation Service (ReST service)

Running `mvn spring-boot:run` or runnning `net.finmath.smartcontract.service.Appplication` starts a
ReST service providing a valuation oracle.

The enpoint `https://localhost:8080/valuation/value` allows the valuation of a financial product under given market data.

The enpoint `https://localhost:8080/valuation/margin` allows the calculation of the settlement amount between two market data sets.

The market data has to be provided as a JSON.
The product data as to be provided as an XML (containing a part being an FPML of the underlying product).

See also `api.yml`.

#### Value

The endpoint value calculates the value of a financial product
with given market data.

The endpoint parameters are
- product P
- market data M
- valuation time t (see note below)

**Note**: The valuation time t is currently taken from the market data set M

The result is the value
- V(P,M,t)

**Note**: The valuation time t is currently taken from the market data set M1

#### Margin

The enpoint parameters are
- product P
- market data M0 (market data at previous margin call or initial valuation)
- market data M1 (market data for margin call)
- valuation time t (see note below)

The result is the value
- M(P,M0,M1,t) = V(P,M1,t) - V(P,M0,t)

**Note**: The valuation time t is currently taken from the market data set M1

#### Valuation Library

The underlying valuation library is [finmath lib](https://finmath.net/finmath-lib).

### Settlement Amount Oracle and Valuation Oracle

The package `net.finmath.smartcontract.oracle` contains the interface describing a so called oracle providing the
settlement amount for a smart derivative contract. The package contains also an implementation for interest rate swaps.

### State Machine Model for a Smart Derivative Contract

The package `net.finmath.smartcontract.statemachine` contains a simple state machine modeling a smart derivative
contract.

## Financial Product Description

The smart derivative contract is described in terms of the sdc.xml.

For a sample XML and the XSD see `resources/net/finmath/smartcontract/product/xml`.

## Docker (for Valuation Service)

To run Docker Container execute following commands.

*Note*: It is important that you run the correct version. The repository comes with release tags.

Clone this repository, if not done yet:
```
git clone https://github.com/finmath/finmath-smart-derivative-contract.git
cd finmath-smart-derivative-contract
```

### Build the Docker Container

```
docker build -t valuation_service .
```

### Run the Docker Container

**Important:** provide users and passwords via an application.yml file that resides
in `/PATH/TO/YOUR/CONFIG` (on the machine running Docker).

```
docker run -v /PATH/TO/YOUR/CONFIG:/config -p 8080:8080 valuation_service
```

Alternative: Use Maven to build the Docker image (without Dockerfile)

```
mvn spring-boot:build-image
docker run -v /PATH/TO/YOUR/CONFIG:/config -p 8080:8080 docker.io/library/finmath-smart-derivative-contract:0.1.8-SNAPSHOT
```

### Config

A sample `application.yml` is
```
data:
  sdc:
    users:
      - username: user1
        password: password1
        role: USER_ONE
      - username: user2
        password: password2
        role: USER_TWO
```

### Testing the Valuation Service

Run
```
./scripts/test-margin-valuation-oracle.sh user:password
```
where `user` is a username configured in the `application.yml` (in `/PATH/TO/YOUR/CONFIG`)
and  `password` is the corresponding password configured in the `application.yml` (in `/PATH/TO/YOUR/CONFIG`) .

## Developer Resources

### Languages and Build

The project requires Java 17 or better.

The Maven build file is provide. Import the project as Maven project.

### Distribution

finmath smart-derivative-contract is distributed through the central maven repository. It's coordinates are:

```
	<groupId>net.finmath</groupId>
	<artifactId>finmath-smart-derivative-contract</artifactId>
	<version>${project.version}</version>
```

### Coding Conventions
-------------------------------------

We follow losely the Eclipse coding conventions, which are a minimal modification of the original Java coding
conventions. See https://wiki.eclipse.org/Coding_Conventions

We deviate in some places. See [coding conventions](coding/codingconventions.md) for details.

## License

The code is distributed under the [Apache License version 2.0][], unless otherwise explicitly stated.

[Apache License version 2.0]: http://www.apache.org/licenses/LICENSE-2.0.html


