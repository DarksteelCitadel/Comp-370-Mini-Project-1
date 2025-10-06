#!/bin/bash
# delay-heartbeat.sh — simulate heartbeat delay for a backup server

BACKUP_ID=2  # default backup server to delay
DELAY=5      # seconds

echo "Delaying heartbeat for Backup Server $BACKUP_ID..."
# Assuming your BackupServer class has a scriptable delay interface
# This will require your BackupServer to check a "delay" file or env var
touch scripts/delay_$BACKUP_ID
sleep $DELAY
rm scripts/delay_$BACKUP_ID
echo "Backup Server $BACKUP_ID heartbeat resumed."
