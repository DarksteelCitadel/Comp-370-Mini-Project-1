#!/bin/bash
# kill-primary.sh — terminate the primary server

PID_FILE="scripts/.pids.primary"

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    kill -9 $PID
    echo "Primary server ($PID) killed."
else
    echo "Primary server PID not found."
fi
