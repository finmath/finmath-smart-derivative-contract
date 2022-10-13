# Move to top level
SCRIPT_PATH="$(dirname "$0")"
cd $SCRIPT_PATH/../
echo $PWD

# Start
mvn clean spring-boot:run