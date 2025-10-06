#!/bin/bash
# Simulate a delay in heartbeat handling

# This pauses the primary server’s heartbeat sending for 5 seconds
# Adjust "Main primary" to the process you want to pause
PID=$(pgrep -f "Main primary")

if [ -z "$PID" ]; then
  echo "Primary server is not running."
  exit 1
fi

echo "Pausing primary server heartbeat for 5 seconds..."
kill -STOP $PID
sleep 5
kill -CONT $PID
echo "Heartbeat resumed."
