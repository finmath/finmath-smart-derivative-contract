# finmath smart derivative contracts

- - - -
**Tools and analytics for smart derivative contracts.**
- - - -

The project is work in progress.

The project provides tools and analytics related to smart derivative contracts.

## Literature

See https://ssrn.com/abstract=3163074

## Contents

### Visualization and Demonstration

The package `net.finmath.smartcontract.demo`contains a visualization of a smart derivative contract.
This is a Java FX application. Run `VisualiserSDC`.

### Valuation Service (ReST service)

Running `mvn spring-boot:run` or runnning `net.finmath.smartcontract.service.Appplication` starts a
ReST service providing a valuation oracle.

The enpoint https://localhost:8080/valuation/value allows the valuation of a financial product under given market data.

The enpoint https://localhost:8080/valuation/margin allows the calculation of the settlement amount between two market data sets.

The market data has to be provided as a JSON.
The product data as to be provided as an XML (containing a part being an FPML of the underlying product).

See also `api.yml`.

### Settlement Amount Oracle and Valuation Oracle

The package `net.finmath.smartcontract.oracle` contains the interface describing a so called oracle providing the
settlement amount for a smart derivative contract. The package contains also an implementation for interest rate swaps.

### State Machine Model for a Smart Derivative Contract

The package `net.finmath.smartcontract.statemachine` contains a simple state machine modeling a smart derivative
contract.

## Product

The smart derivative contract is described in terms of the sdc.xml.

## Docker

To run Docker Container execute following commands

```
mvn clean package spring-boot:repackage
docker build -t valuation_service .
docker run -p 8080:8080 valuation_service
```

