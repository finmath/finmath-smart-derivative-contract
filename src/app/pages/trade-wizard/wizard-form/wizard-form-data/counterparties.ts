export class Counterparty{
  id: string;
  fullName: string;

  constructor(id: string, fullName: string){
    this.id = id;
    this.fullName = fullName;
  }
}

export const counterparties: Counterparty[] = [
  {
    id: 'P1',
    fullName: 'Party One Bank',
  },
  {
    id: 'P2',
    fullName: 'Party Hard Capital',
  },
  {
    id: 'P3',
    fullName: 'Party Rock Investment',
  }
];
