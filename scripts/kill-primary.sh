#!/bin/bash
# kill-primary.sh — kill the current PrimaryServer JVM

PRIMARY_PID=$(ps aux | grep "[j]ava -cp out PrimaryServer" | awk '{print $2}' | head -n 1)

if [ -z "$PRIMARY_PID" ]; then
    echo "PrimaryServer is not running."
    exit 1
fi

kill -9 $PRIMARY_PID
echo "PrimaryServer with PID $PRIMARY_PID killed."

