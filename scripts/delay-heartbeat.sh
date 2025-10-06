#!/bin/bash
# delay-heartbeat.sh — simulate delayed heartbeats for PrimaryServer

# Find the PID of the PrimaryServer using ps (works even across tabs)
PRIMARY_PID=$(ps aux | grep "[j]ava -cp out PrimaryServer" | awk '{print $2}' | head -n 1)

if [ -z "$PRIMARY_PID" ]; then
    echo "PrimaryServer is not running."
    exit 1
fi

echo "Found PrimaryServer with PID: $PRIMARY_PID"

# Simulate heartbeat delay by stopping and resuming
echo "Simulating heartbeat delay..."
kill -STOP $PRIMARY_PID
sleep 5
kill -CONT $PRIMARY_PID

echo "PrimaryServer heartbeat resumed."
