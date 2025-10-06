#!/bin/bash
# Simulate heartbeat delay
# This assumes your BackupServer or Monitor code supports a heartbeat delay flag
# If not, this just prints a message
echo "Simulating heartbeat delay..."
# Example: send a signal to your monitor or backup to delay heartbeats
# pkill -STOP -f "Main backup1"  # temporarily stop process
sleep 5
# pkill -CONT -f "Main backup1"  # resume process
echo "Heartbeat delay triggered."
