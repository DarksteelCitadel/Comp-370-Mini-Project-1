#!/bin/bash

# Compile Java files
echo "Compiling Java files..."
javac src/*.java -d out
mkdir -p out

PID_FILE="scripts/.pids"
> $PID_FILE

# Start Monitor
echo "Starting Monitor..."
java -cp out Monitor &
echo $! >> $PID_FILE
sleep 1

# Start Primary Server
echo "Starting Primary Server..."
java -cp out PrimaryServer 1 6000 &
echo $! >> $PID_FILE
sleep 1

# Start Backup Server 2
echo "Starting Backup Server 2..."
java -cp out BackupServer 2 6001 &
echo $! >> $PID_FILE
sleep 1

# Start Backup Server 3
echo "Starting Backup Server 3..."
java -cp out BackupServer 3 6002 &
echo $! >> $PID_FILE
sleep 1

# Start Admin Interface
echo "Starting Admin Interface..."
java -cp out AdminInterface &
echo $! >> $PID_FILE
sleep 1

echo "All components started!"






