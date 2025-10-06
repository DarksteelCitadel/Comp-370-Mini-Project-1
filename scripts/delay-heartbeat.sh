#!/bin/bash
PID_FILE="scripts/.pids"

PRIMARY_PID=$(grep "PrimaryServer" $PID_FILE | cut -d':' -f2)

if [ -z "$PRIMARY_PID" ]; then
    echo "Primary server is not running."
    exit 1
fi

echo "Found PrimaryServer with PID: $PRIMARY_PID"

echo "Simulating heartbeat delay..."
kill -STOP $PRIMARY_PID   # Pause process
sleep 5                   # Adjust delay if needed
kill -CONT $PRIMARY_PID   # Resume process

echo "PrimaryServer heartbeat resumed."


