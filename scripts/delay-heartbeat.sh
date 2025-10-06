#!/bin/bash
# delay-heartbeat.sh — simulate delayed heartbeats for PrimaryServer

PID_FILE="scripts/.pids"

if [ ! -f "$PID_FILE" ]; then
  echo "PID file not found. Run run.sh first."
  exit 1
fi

PRIMARY_PID=$(grep "PrimaryServer:" "$PID_FILE" | cut -d':' -f2)

if [ -z "$PRIMARY_PID" ]; then
  echo "PrimaryServer PID not found in $PID_FILE"
  exit 1
fi

if ! ps -p "$PRIMARY_PID" > /dev/null; then
  echo "PrimaryServer (PID $PRIMARY_PID) is not running."
  exit 1
fi

echo "Simulating heartbeat delay for PrimaryServer (PID $PRIMARY_PID)..."
kill -STOP "$PRIMARY_PID"
sleep 5
kill -CONT "$PRIMARY_PID"
echo "Heartbeat delay complete."

