
# Move to top level
SCRIPT_PATH="${BASH_SOURCE:-$0}"
cd $SCRIPT_PATH/../../
echo $PWD

export SDC_HOME=src/main/deploy

echo "Note: The code does not run under Java 17, but runs under Java 11 - due to incompatiblity with Java module system"

# Start
mvn clean spring-boot:run -Dspring-boot.run.arguments=sdc