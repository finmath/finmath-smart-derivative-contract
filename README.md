# finmath smart derivative contracts

- - - -
**Tools and analytics for smart derivative contracts.**
- - - -

The project is work in progress.

The project provides tools and analytics related to smart derivative contracts.

## Literature

See https://ssrn.com/abstract=3163074

## Contents

### State Machine Model for a Smart Derivative Contract

The package `net.finmath.smartcontract.statemachine` contains a simple state machine modeling a smart derivative
contract.

### Settlement Amount Oracle and Valuation Oracle

The package `net.finmath.smartcontract.oracle` contains the interface describing a so called oracle providing the
settlement amount for a smart derivative contract. The package contains also an implementation for interest rate swaps.

### Visualization and Demonstration

The package `net.finmath.smartcontract.demo`contains a visualization of a smart derivative contract. This is a Java FX
application.

## Docker
To run Docker Container execute following commands
```
mvn clean package spring-boot:repackage
docker build -t valuation_service .
docker run -p 8080:8080 valuation_service
```

