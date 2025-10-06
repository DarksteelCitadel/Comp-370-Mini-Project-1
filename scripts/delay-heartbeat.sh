#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: ./delay-heartbeat.sh <backup-id>"
  exit 1
fi

BACKUP_ID=$1
DELAY_FILE="scripts/delay_$BACKUP_ID"

echo "Delaying heartbeat for Backup Server $BACKUP_ID..."
touch $DELAY_FILE

# Wait 5 seconds (or however long you want the delay)
sleep 5

# Remove delay file to resume heartbeat
rm -f $DELAY_FILE
echo "Backup Server $BACKUP_ID heartbeat resumed."
