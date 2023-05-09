import { Counterparty } from "src/app/openapi/model/counterparty";

/**
 * List of counterparties available for selection.
 */
export const counterparties: Counterparty[] = [
  {
    bicCode: "P1BKDEMM",
    fullName: "Party One Bank",
    baseUrl: "https://www.p1bank.de/",
  },
  {
    bicCode: "PHCAIT3S",
    fullName: "Party Two Capital",
    baseUrl: "https://www.partytwocapital.it/",
  },
  {
    bicCode: "PRCKCHZH",
    fullName: "Party Three Investment",
    baseUrl: "https://zentrale.p3.ch/",
  },
];
