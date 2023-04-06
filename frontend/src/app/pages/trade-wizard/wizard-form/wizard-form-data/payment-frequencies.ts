import { PaymentFrequency } from "src/app/openapi";

export const paymentFrequencies: PaymentFrequency[] = [
  {
    period: 'M',
    periodMultiplier: 6,
    fullName: 'Semiannual',
  },
  {
    period: 'M',
    periodMultiplier: 1,
    fullName: 'Monthly',
  },
  {
    period: 'M',
    periodMultiplier: 3,
    fullName: 'Quarterly',
  },
  {
    period: 'D',
    periodMultiplier: 1,
    fullName: 'Daily',
  },
  {
    period: 'Y',
    periodMultiplier: 1,
    fullName: 'Annual',
  },
];
