# Sketch of a decentralised digital post-trade infrastructure for OTC-Derivatives
In the following we collect some thoughts whether a decentralized and digital infrastructure equipped with 
a highly automated and algorithmic post-trade processing protocols may offer advantages compared to established centralized processing approaches. 
We shortly review the concept of a Smart Derivative Contract as a key element of such a digital post-trade processing solution. 
We provide a scetch of how a decentralised and automatable post-trade processing landscape may look like.

## Key charateristics
The following elements may serve as cornerstones of a digital post-trade processing landscape:
* A decentralised, fault-tolerant and digital infrastructure is available on which counterparties can onboard
* Counterparties can trade and process OTC-based contracts in a bilateral manner by using standardized data formats (e.g. fpml), which completely specify the economics of the contract
* Usage of market data is enabled and a set standardized valuation algorithms to calculate contracts net present value is available
* A digital post-trade protocol standard exists which enables automatic settle-2-market of the contract value in terms that it resets the contract market value in pre-agreed settlement periods
* Intraday-Settlement to enable resetting due to large market moves is enabled.
* Identification and processing of a failure to pay is captured in a deterministic manner
* There exists a protocol which is responsible to rematch open positions between counterparties post-default
* Counterparties can terminate and re-incept OTC-contracts prematurely on preagreed terms to capture technical or methodological failures.

## Review of the concept of a Smart Derivative Contract

### 
The concept of a Smart Derivative Contract was introduced in [Smart Derivative Contract - Detaching Transactions from counterparty credit risk](https://ssrn.com/abstract=3163074). 
A Smart Derivative Contract is a deterministic settlement protocol for collateralized OTC derivatives.
A periodic settlement amount determined according to fixed rules is automatically booked using an agreed prefunding amounts 
that the parties cannot dispose of during a settlement period. 
The protocol automatically terminates the derivatives contract prematurely if there is insufficient prefunding provided by one or both counterparties.

## Key features of a Smart Derivative Contract 
Compared to current post-trade landscape the concept of a Smart Derivative Contract is characterized by the following features:
* The valuation of the net present value is fully specified such that it can be handled completely algorithmical
* The concept resembles the economics of a collateralized OTC derivative, it nets collateral and contract based cash flows - so-called "settle-2-market"
* Potential settlement amounts get prefunded by both counterparties up to a contractually agreed margin buffer
* Termination is a contract based feature and may occur if the settlement amount exceeds the margin buffer or if prefunding is not provided sufficiently.
* In case of termination a termination fee is transfered automatically from the causing party. Termination fee can be seen as an contractual agreed initial margin. The concept is fully deterministic and automatable such that it can be implemented as a digital protocol on a centralized or decentralized infrastructure.

We describe some of the features in more detail:

### Contractrually agreed valuation methodology
Valuation methodology is a contractual feature where calculation of the net present value of the contract 
is fully specified in terms of market data usage, numerical algorithms and software version. 
This core feature removes dispute resolution processes and enables full-automated settle-2-market processing. 
When linked to an automated payment solution the process can scale in terms of settlement frequency. 
If one or both counterparties come to conclusion that valuation methodology might not be applicable further they can trigger termination either one-sided - which would imply that the termination fee is transferred or consensual which may not trigger a termination fee flow.

### Event based termination
The concept aims to detach otc based transactions from counterparty risk assigning each contract an individual termination probability. 
In its pure form termination is based on two events. Either if funding is not provided or based on market movements which suceed the agreed prefunding amounts.
As termination due to insufficient pre-funding can be determined right after settlement the contract will terminate with zero market value, leaving termination fee the only flow which is transferred in addition. Second termination event, if market movement exceeds the agreed buffers, there will be a flow of the full margin buffer plus termiation fee. In any case each smart derivatives based contract cash flow will be capped at that amount.

### Termination Risk versus Default Risk.
Termination and termination procedure is fully event specific, contractually agreed and deterministic. Compared to a regular otc derivative termination can take place at each agreed settlement time points. With a view of default or liquidity squeeze scenario, termination might be triggered by providing insufficient liquidity. As soon as margin buffer prefunding is provided, parties can be sure that settlement will be processed as long as settlement amount exceeds the agreed margin buffer. The case of termination due to high settlement values occurs might seem critical on the first view but there might be several enhancements where that risk might be managable.
In a direct comparison in case of default of regular otc based contracts a default either triggers bilateral or centralised default resolution. In the first case each connected counterparty needs to close its open position while struggeling on the other hand to get to know when default is published and collateral and contract cash flows are put on hold. In the second case all members need to resolve the open position of the ccp. First one seems to second one might face the problem of missing incentives. In both cases time delay might play a critical role leading to scenarios which might won't be predicable.

### Mitigation techniques for termination risk and requirements
As in the previous section default resolution either in centralized or bilateral form it seems not possible to fully predict the entire default resolution process since it depends on the behaviour of multiple market participant acting in a probable stressed market scenario where different strategies may occur which have not been predictable before. The smart contract case is different: Settlement is guaranteed up to a certain amount which is clearly subject to negotiation. Conservative counterparties margin buffer amount should be chosen high enough to shrink termination probability to lowest levels as possible. 
The termination event is completely specified. After each settlement there is complete knowledge whether the contract is terminated or not. Put in this case the derivatives market value is settled, termination fee is transferred. Simply put market participants which face an open position may turn to others and can incept new trades offsetting their open positions which resulted from the early termination event. Algorithmic approaches can be scetched also for this situation and will be described down below.

### Zero initial payment
An interesting effect maybe benificial compared to the traditional way of reinception. 
As mentioned earlier the smart derivative resembles an collateralized otc derivative with the advantage that contract based and collteral flows are netted. Compare the classical bilateral world after default occurence with that new approach. Let's imagine the non-defaulting counterparty holding a high positive market value position against the defaulted counterparty. To close this position the non-defaulting counterparty take the collateral received and turn to a other trading institution to re-incept the off-market trade. For that the non-defaulting counterparty takes the collateral received for paying the market value of the trade. But due to the delay collateral movements that amount will be transferred back with the first collateral movement exposing the non-defaulting counterparty again to uncollateralized counterparty exposure. In the smart derivative case, there is no upfront payment since collateral and market value payment would be netted, so contracts can be incepted - modulo initial fees - around zero which further eases the inception procedure. Also note the fact that received termination fees might be used to incentivise other trading counterparts to close the counterparties position at a slight off-market niveau.

### Possible improvements of the original protocol
The original proposed protocol can be modified and enhanced in several dimensions.

* Digital trade inception

* Scaling in product dimension

* Intraday settlement and settlement per request

* Termination as a feature

* Nettingset based processing

Further details to follow.


