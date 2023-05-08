/**
 * Interface representing a fixing offset measured in days.
 */
export interface FixingDayOffset {
  /**
   * Univoque identification of the fixing offset. Should be intended as the key for all selection purposes
   */
  id: string;

    /**
   * Human readable description of the fixing offset
   */
  fullName: string;
}

/**
 * List of fixing offsets (measured in days) recognized by the valuation oracle.
 */
export const fixingDayOffsets: FixingDayOffset[] = [
  {
    id: "-2",
    fullName: "-2",
  },
  {
    id: "-1",
    fullName: "-1",
  },
  {
    id: "0",
    fullName: "0",
  },
  {
    id: "1",
    fullName: "1",
  },
  {
    id: "2",
    fullName: "2",
  },
];
