#!/usr/bin/env bash

SCRIPT_PATH="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
echo $SCRIPT_PATH

# Move to top level
cd $SCRIPT_PATH/../
echo $PWD

# Start
mvn clean spring-boot:run