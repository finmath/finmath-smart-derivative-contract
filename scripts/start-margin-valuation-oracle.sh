
# Move to top level
SCRIPT_PATH="$(dirname "$0")"
cd $SCRIPT_PATH/../
echo $PWD

export SDC_HOME=src/main/deploy

# Start
mvn clean spring-boot:run -Dspring-boot.run.arguments=sdc