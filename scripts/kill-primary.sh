#!/bin/bash
if [ ! -f primary.pid ]; then
  echo "Error: primary.pid not found. Run run.sh first."
  exit 1
fi

PRIMARY_PID=$(cat primary.pid)
echo "Killing Primary Server (PID $PRIMARY_PID)..."
kill -9 $PRIMARY_PID
echo "Primary server killed."
