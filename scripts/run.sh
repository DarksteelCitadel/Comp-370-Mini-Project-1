#!/bin/bash
# run.sh — start monitor, servers, admin interface, record PIDs

# Create output directories
mkdir -p out
rm -f scripts/.pids

# Start Monitor
echo "Starting Monitor..."
java -cp out Monitor &
echo $! > scripts/.pids.monitor
sleep 1

# Start PrimaryServer
echo "Starting PrimaryServer..."
java -cp out PrimaryServer 1 6000 &
echo $! > scripts/.pids.primary
sleep 1

# Start BackupServers
echo "Starting BackupServer 2..."
java -cp out BackupServer 2 6001 &
echo $! > scripts/.pids.backup1
sleep 1

echo "Starting BackupServer 3..."
java -cp out BackupServer 3 6002 &
echo $! > scripts/.pids.backup2
sleep 1

# Start AdminInterface in background
echo "Starting AdminInterface..."
java -cp out AdminInterface &
echo $! > scripts/.pids.admin

echo "All components started! Monitor the terminal for heartbeats."






