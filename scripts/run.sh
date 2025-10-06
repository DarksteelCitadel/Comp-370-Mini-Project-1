#!/bin/bash
# Make scripts executable
chmod +x scripts/*.sh
mkdir -p scripts/.pids

# Compile
javac src/*.java -d out

# Start Monitor
java -cp out Monitor 7100 &
echo $! > scripts/.pids/monitor.pid
sleep 1

# Start Primary Server
java -cp out PrimaryServer 1 6000 &
echo $! > scripts/.pids/primary.pid
sleep 1

# Start Backup Servers
java -cp out BackupServer 2 6001 &
echo $! > scripts/.pids/backup2.pid
sleep 1

java -cp out BackupServer 3 6002 &
echo $! > scripts/.pids/backup3.pid
sleep 1

# Start Admin Interface
java -cp out AdminInterface &
echo $! > scripts/.pids/admin.pid
sleep 1

echo "All components started!"





