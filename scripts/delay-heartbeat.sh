#!/bin/bash
BACKUP2_PID_FILE="scripts/.pids/backup2.pid"
if [ -f "$BACKUP2_PID_FILE" ]; then
    echo "Delaying heartbeat for Backup Server 2..."
    kill -STOP $(cat "$BACKUP2_PID_FILE")
    sleep 5
    kill -CONT $(cat "$BACKUP2_PID_FILE")
    echo "Backup Server 2 heartbeat resumed."
else
    echo "Backup Server 2 is not running."
fi


