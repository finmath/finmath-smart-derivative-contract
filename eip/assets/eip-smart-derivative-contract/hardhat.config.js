require("@nomiclabs/hardhat-ethers");
require("@nomicfoundation/hardhat-chai-matchers");
require("solidity-coverage");
require("hardhat-contract-sizer");

module.exports = {
  defaultNetwork: "localhost",
  networks: {
    besu: {
      url: "http://localhost:8545",
      accounts: [
        "b23a98e4b1bbd15dd65a2e1534096ca83280281f7a19c70eebfdd4d3119d05a7", // dz: "0x938ACd0AD9f423E5cDd97c7c880d59799Dc4fDA0"
        "8c3695bd04dd1a6a7d322a0e4897d75c206bd93043a0c7099bcde49b7001dc22", // ui: "0xCE7E5Abf9A42730345716e16c145592e679Ef15a"
      ],
    },
  },
  solidity: {
    compilers: [
      {
        version: "0.6.12",
        settings: {
          optimizer: {
            enabled: true,
            runs: 200,
          },
        },
      },
      {
        version: "0.8.15",
        settings: {
          optimizer: {
            enabled: true,
            runs: 200,
          },
        },
      },
    ],
  },
  paths: {
    // "Explicit is better than implicit."
    sources: "./contracts",
    tests: "./test",
    cache: "./cache",
    artifacts: "./artifacts",
  },
  mocha: {
    // Maybe necessary when testing on-chain.
    timeout: 3000000,
  },
};
