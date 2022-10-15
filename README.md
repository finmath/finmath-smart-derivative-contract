# finmath smart derivative contracts

****************************************

**Algorithms and methodologies related to smart derivative contracts.**

****************************************

## Introduction

The finmath smart-derivative-contract libraries provides (JVM) implementations of methodologies related to smart
derivative contracts.

## Contents

### Visualization and Demonstration

The package `net.finmath.smartcontract.demo`contains a visualization of a smart derivative contract.
This is a Java FX application. Run `VisualiserSDC`.

### Valuation Service (ReST service)

**You may run the valuation service through our Docker image (see below).**

### State Machine Model for a Smart Derivative Contract

The package `net.finmath.smartcontract.statemachine` contains a simple state machine modeling a smart derivative
contract.

## Financial Product Description

The smart derivative contract is described in terms of the sdc.xml.

For a sample XML and the XSD see `resources/net/finmath/smartcontract/product/xml`.

For details see the [project site](https://finmath.net/finmath-smartderivaticecontract).

## Developer Resources

### Languages and Build

The project requires Java 17 or better.

The Maven build file is provide. Import the project as Maven project.

### Distribution

finmath smart-derivative-contract is distributed through the central maven repository.

## License

The code is distributed under the [Apache License version 2.0][], unless otherwise explicitly stated.

[Apache License version 2.0]: http://www.apache.org/licenses/LICENSE-2.0.html


