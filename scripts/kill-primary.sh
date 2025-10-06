#!/bin/bash
# kill-primary.sh - kills the JVM hosting the current PrimaryServer

# Find the PID of the running PrimaryServer
PRIMARY_PID=$(jps -l | grep -i "PrimaryServer" | awk '{print $1}')

if [ -z "$PRIMARY_PID" ]; then
    echo "Primary server is not running."
    exit 1
fi

# Kill the primary server
kill -9 $PRIMARY_PID
echo "Primary server with PID $PRIMARY_PID killed."
