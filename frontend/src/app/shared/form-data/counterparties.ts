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
    fullName: "Party Hard Capital",
    baseUrl: "https://www.partyhardcapital.it/",
  },
  {
    bicCode: "PRCKCHZH",
    fullName: "Party Rock Investment",
    baseUrl: "https://zentrale.prck.ch/",
  },
];
