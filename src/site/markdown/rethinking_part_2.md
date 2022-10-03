# Rethinking Financial Derivatives - Digital Redesign
We try to design a financial derivative from scratch. We start by investigating the complete derivative  lifecycle - from  trade  inception,  valuation,  payments,  collateralization  and termination.
The content of this site is taken from the following [publication](https://papers.ssrn.com/sol3/papers.cfm?abstract_id=3249430)

## Elements of a classical OTC contract
The main elements of a classical OTC derivative product are defined in a so-called “term sheet”. Examples for such terms are variable or fixed interest rate payments, the fixing dates and payment dates and the maturity. In addition, there exists a separate collateral process that  is  defined  in  an  annex.  The  exact  valuation  of  the  derivative  (and  hence  the  precise determination of the collateral) remains undefined.

## Five additional contractual elements reduce risk
We extend the contract by five additional elements. We explicitly define
1. how the derivative has to be valued
2. that this valuation determines a frequent (daily) settlement of the market value,
3. that payments are processed automatically on specific accounts,
4. that the derivative terminates automatically upon specific pre-conditions,
5. that the premature termination results in a fully determined termination process with a deposited termination fee

With  these five  additional  elements we  eliminate  existing  procedural  weaknesses. Associated risks are not present by construction. 
* The valuation is consistent, since it is an agreed clause in the contract. 
* Settlement risks are eliminated since there is only one single net transaction. 
Moreover, the counterparty risk is eliminated since the contract’s termination event is an integral part of the contract itself and fully deterministic.
In summary this can create a market where many counterparties contract bilaterallyin a lightweight and procedural efficient way. As an attractive side effect,the systemic riskcan also get reduced

## Where are the difficulties
Where are the difficulties? The  above changes  primarily require  fundamental  adjustments  to  the  current  process landscape.  
The  collateral  process  is  no  longer  needed  since  it  is  replaced  by  a  daily settlement.  
The  daily  settlement  operates  on  a  small  net  amount stemming  frommarket value  changes  only,  since  product  cash-flows  are  already  netted.  Consistent  and  unified valuation has to be performed independently and in a transparent manner.
Finally,  the  termination  criteria  have  to  be  monitored  and  termination  has  to  be  enforced independently of the counterparties.

## Smart Contracts and Distributed Ledger Technology
There  is  an ongoing  discussion  around  whether  ideas  from  distributed  ledger  technology  (DLT)  are able  to  find  application  
in  some  areas  of  economic  life  with  the  aim  of  standardizing  and automating  existing  processes.  
The  basic  idea  seems  attractive:  A  separate  private accounting system of transactions generates redundancies and requires a high reconciliation effort  for  every  participating  party.  In  contrast,  within  a  DLT  a  unique  and  public  “ledger” would  exist  which  would  result  in  a  unique  definition  and  understanding  of  transactions.
Some  DLT  systems  also  support  so-called  “Smart  Contracts”:  Algorithm  based  computer protocols  on  which  arbitrary  transactions  can  be  processed  automatically  and  in  a standardized fashion.

## Smart Contract + Derivative = Smart Derivative Contract
Each of our introduced contractual terms -e.g. unique valuation -in the derivative contract present  a  deterministic  algorithm  by  construction.  Uncertainty  at  any  process  state  of  a derivative life-cycle is removed. Therefore,those terms can be represented by a computer program.  If  we  implement a  term  sheetwith  the  amendments  above  in  an  algorithmically based smart contract, all contract modalities of a derivative will be digitalised. The gain will be twofold:  Full clarity of all contract terms as well as the possibility of full automatization of all relevant life-cycle processes.

## Conclusion: Rethinking is needed
Not more regulation, but proactive improvements of risk and process culture is needed to further  reduce  risks  and  costs  in  derivative  markets.  The  application  of computer-basedalgorithms–Smart  Contracts–seems  attractive  to  increase  standardization  and  to  remove existing uncertainty. The good thing: With new process definitions we are able to improve risk management or even better to eliminate risk entirely.New  process  definitions  will  not  only  require  new  technology,  but  especially  a  holistic approach  across  several  business  departments.  This  might  be  one  of  the  challenges  in financial industry for the next years to come.

## Further Reading
For further detailed reading we refer to the original publication [Smart Derivative Contract - Detaching Transactions from counterparty credit risk](https://ssrn.com/abstract=3163074)