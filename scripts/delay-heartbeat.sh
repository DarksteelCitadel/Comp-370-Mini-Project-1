#!/bin/bash
# delay-heartbeat.sh - simulate delayed heartbeats for the primary server

# Find the PID of the running PrimaryServer
PRIMARY_PID=$(jps -l | grep -i PrimaryServer | awk '{print $1}')

if [ -z "$PRIMARY_PID" ]; then
    echo "Primary server is not running."
    exit 1
fi

echo "Found PrimaryServer with PID: $PRIMARY_PID"

# Simulate heartbeat delay by stopping the process for a few seconds
echo "Simulating heartbeat delay..."
kill -STOP $PRIMARY_PID  # pause the process
sleep 5                  # delay time (adjust if needed)
kill -CONT $PRIMARY_PID  # resume the process

echo "PrimaryServer heartbeat resumed."
