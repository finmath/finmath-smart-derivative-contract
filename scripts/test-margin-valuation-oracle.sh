echo "Note: The valuation oracle has to run."

# Move to top level
SCRIPT_PATH="$(dirname "$0")"
cd $SCRIPT_PATH/../
echo $PWD

mvn compile exec:java -Dexec.mainClass=net.finmath.smartcontract.client.ValuationClient -Dexec.args="$1 $2"
