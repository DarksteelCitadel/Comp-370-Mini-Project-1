#!/bin/bash
# Compile all Java files
echo "Compiling project..."
mkdir -p out
javac src/*.java -d out

# Start the Monitor
echo "Starting Monitor..."
java -cp out Monitor &
MONITOR_PID=$!
sleep 1

# Start Primary Server
echo "Starting Primary Server..."
java -cp out PrimaryServer 1 6000 &
PRIMARY_PID=$!
sleep 1

# Start Backup Server 2
echo "Starting Backup Server 2..."
java -cp out BackupServer 2 6001 &
BACKUP1_PID=$!
sleep 1

# Start Backup Server 3
echo "Starting Backup Server 3..."
java -cp out BackupServer 3 6002 &
BACKUP2_PID=$!
sleep 1

# Start Admin Interface
echo "Starting Admin Interface..."
java -cp out AdminInterface &
ADMIN_PID=$!
sleep 1

# Start Client
echo "Starting Client..."
java -cp out Client &
CLIENT_PID=$!

# Save all process IDs
echo "Monitor PID: $MONITOR_PID"
echo "Primary PID: $PRIMARY_PID"
echo "Backup1 PID: $BACKUP1_PID"
echo "Backup2 PID: $BACKUP2_PID"
echo "Admin PID: $ADMIN_PID"
echo "Client PID: $CLIENT_PID"

# Write PIDs to a file for use in other scripts
echo "$PRIMARY_PID" > primary.pid
echo "$MONITOR_PID" > monitor.pid
echo "$BACKUP1_PID" > backup1.pid
echo "$BACKUP2_PID" > backup2.pid
echo "$ADMIN_PID" > admin.pid
echo "$CLIENT_PID" > client.pid

echo "System started. Use Ctrl+C to stop or run kill-primary.sh to simulate failure."
