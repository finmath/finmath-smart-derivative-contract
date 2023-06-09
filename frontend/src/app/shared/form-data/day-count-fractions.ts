/**
 * Interface representing a day count fraction recognized by the valuation oracle.
 */
export interface DayCountFraction {
  /**
   * Univoque identification of the day count fraction. Should be intended as the key for all selection purposes.
   */
  id: string;

  /**
   * Human readable description of the day count fraction.
   */
  fullName: string;
}

/**
 * List of day count fractions recognized by the valuation oracle.
 */
export const dayCountFractions: DayCountFraction[] = [
  {
    id: "ACT/ACT",
    fullName: "ACT/ACT",
  },
  {
    id: "ACT/360",
    fullName: "ACT/360",
  },
  {
    id: "30E/360",
    fullName: "30E/360",
  },
];
