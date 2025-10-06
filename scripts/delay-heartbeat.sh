#!/bin/bash
PID_FILE="scripts/.pids"

PRIMARY_PID=$(head -n 1 $PID_FILE) # first line is PrimaryServer

if [ -z "$PRIMARY_PID" ]; then
    echo "Primary server PID not found."
    exit 1
fi

echo "Simulating heartbeat delay..."
kill -STOP $PRIMARY_PID
sleep 5
kill -CONT $PRIMARY_PID
echo "Primary server heartbeat resumed."



