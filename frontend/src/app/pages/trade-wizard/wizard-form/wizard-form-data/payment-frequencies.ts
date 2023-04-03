export interface PaymentFrequency{
  id: string,
  fullName: string
}

export const paymentFrequencies: PaymentFrequency[] = [
  {
    id: '12',
    fullName: '12 months',
  },
  {
    id: '6',
    fullName: '6 months',
  },
];
