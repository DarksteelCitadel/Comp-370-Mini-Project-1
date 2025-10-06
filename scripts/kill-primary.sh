#!/bin/bash
PID_FILE="scripts/.pids"

PRIMARY_PID=$(grep "PrimaryServer" $PID_FILE | cut -d':' -f2)

if [ -z "$PRIMARY_PID" ]; then
    echo "Primary server is not running."
    exit 1
fi

kill -9 $PRIMARY_PID
echo "Primary server killed (PID $PRIMARY_PID)."
