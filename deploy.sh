#!/bin/bash

if [ $2 = "check" ]; then
    mvn versions:set -DnewVersion=$1-SNAPSHOT -DoldVersion=1.0-SNAPSHOT
    mvn clean deploy -Ddeploy.version=$1 -P release
    mvn versions:revert
elif [ $2 = "stage" ]; then
    mvn versions:set -DnewVersion=$1 -DoldVersion=1.0-SNAPSHOT
    mvn clean deploy -P release
    mvn versions:revert
elif [ $2 = "npm" ]; then
    mvn clean deploy -P releaseNpm -Ddeploy.version=$1
fi
