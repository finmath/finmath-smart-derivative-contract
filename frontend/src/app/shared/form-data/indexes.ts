import { PaymentFrequency } from "src/app/openapi";

export interface Index{
  name: string;
  frequency: PaymentFrequency;
}
/**
 * List of counterparties available for selection.
 */
export const indexes: Index[] = [
  {
    name: "EURIBOR 6M",
    frequency: {
      period: "M",
      periodMultiplier: 6,
      fullName: "Semiannual",
    }
  }
];
