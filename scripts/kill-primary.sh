#!/bin/bash

# Find the PID of PrimaryServer
PRIMARY_PID=$(jps -l | grep PrimaryServer | awk '{print $1}')

if [ -z "$PRIMARY_PID" ]; then
  echo "Primary server PID not found."
else
  echo "Killing PrimaryServer with PID $PRIMARY_PID..."
  kill -9 $PRIMARY_PID
  echo "PrimaryServer killed."
fi
