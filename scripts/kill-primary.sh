#!/bin/bash
# Kill the Java process running the primary server
PRIMARY_PID=$(jps -l | grep -i "PrimaryServer 1" | awk '{print $1}')

if [ -z "$PRIMARY_PID" ]; then
    echo "Primary server is not running."
    exit 1
fi

kill -9 $PRIMARY_PID
echo "Primary server killed."
