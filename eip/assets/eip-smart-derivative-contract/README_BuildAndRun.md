# SDC Solidity implementation

## Description
This sdc implementation aims to implement process logic in a very lean way using an integrative solidity implementation and according unit tests

## Provided Contracts
### 1. Smart Derivative Contract


## Getting started with Visual Studio and Hardhat:
A good getting started can be found [here](https://blog.oliverjumpertz.dev/how-to-set-up-a-solidity-project-and-create-your-first-smart-contract)
We provide the essential steps in the following, assuming NodeJS 14.x LTS ist installed.

Check that you have the latest version of npm and node via `npm -version` (should be better than 8.5.0) and `node -v` (should be better than 16.14.2).

1. Check out project
2. Go to folder and initialise a new npm project: `npm init -y`. A basic `package.json` file should occur
3. Install Hardhat as local solidity dev environment: `npx hardhat`
4. Select: Create an empty hardhat.config.js and change solidity compiler version to 0.8.4
5. Install Hardhat as a development dependency: `npm install --save-dev hardhat`
6. Install further testing dependencies:
`npm install --save-dev @nomiclabs/hardhat-waffle @nomiclabs/hardhat-ethers ethereum-waffle chai  ethers solidity-coverage`

7. `npm install @openzeppelin/contracts`

8. add plugins to hardhat.config.ts: 
```
require("@nomiclabs/hardhat-waffle"); 
require('solidity-coverage');
```

9. Adding commands to `package.json`:
``` 
"scripts": {
    "build": "hardhat compile",
    "test:light": "hardhat test",
    "test": "hardhat coverage"
  },
```
9. run `npm run build`
10. run `npm run test`

## Javascript based testing libraries for solidity
- `ethereum-waffle`: Waffle is a Solidity testing library. It allows you to write tests for your contracts with JavaScript.
- `chai`: Chai is an assertion library and provides functions like expect.
- `ethers`: This is a popular Ethereum client library. It allows you to interface with blockchains that implement the Ethereum API.
- `solidity-coverage`: This library gives you coverage reports on unit tests with the help of Istanbul.

