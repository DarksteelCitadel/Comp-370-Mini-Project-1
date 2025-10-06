#!/bin/bash

# Compile Java files
echo "Compiling Java files..."
javac src/*.java -d out

mkdir -p out

# Start Monitor
echo "Starting Monitor on port 7100..."
osascript -e 'tell application "Terminal" to do script "java -cp out Monitor"'

# Start Primary Server
echo "Starting Primary Server (ID 1, port 6000)..."
osascript -e 'tell application "Terminal" to do script "java -cp out PrimaryServer 1 6000"'

# Start Backup Server 2
echo "Starting Backup Server (ID 2, port 6001)..."
osascript -e 'tell application "Terminal" to do script "java -cp out BackupServer 2 6001"'

# Start Backup Server 3
echo "Starting Backup Server (ID 3, port 6002)..."
osascript -e 'tell application "Terminal" to do script "java -cp out BackupServer 3 6002"'

# Start Admin Interface
echo "Starting Admin Interface..."
osascript -e 'tell application "Terminal" to do script "java -cp out AdminInterface"'

echo "All components started!"

