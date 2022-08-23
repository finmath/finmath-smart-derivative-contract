echo "Note: The valuation oracle has to run."

SCRIPTDIR=$(dirname $0)
export SDC_HOME=$SCRIPTDIR/../src/main/deploy

cd $SCRIPTDIR/..

mvn exec:java -Dexec.mainClass=net.finmath.smartcontract.client.ValuationClient -Dexec.args=sdc
