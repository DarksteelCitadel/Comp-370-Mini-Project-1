#!/bin/bash

# Compile Java files
echo "Compiling Java files..."
javac src/*.java -d out

mkdir -p out
mkdir -p scripts

# File to store PIDs
PID_FILE="scripts/.pids"
> $PID_FILE  # Clear existing file

open_tab() {
  local cmd="$1"
  osascript <<EOF
tell application "Terminal"
  activate
  tell application "System Events" to keystroke "t" using command down
  delay 0.5
  do script "cd '$(pwd)'; $cmd" in front window
end tell
EOF
}

start_process() {
  local cmd="$1"
  local name="$2"
  
  # Start process in background and get PID
  $cmd &
  PID=$!
  
  # Save PID to file
  echo "$name:$PID" >> $PID_FILE
  echo "Started $name with PID $PID"
}

echo "Starting Monitor..."
start_process "java -cp out Monitor" "Monitor"

echo "Starting Primary Server..."
start_process "java -cp out PrimaryServer 1 6000" "PrimaryServer"

echo "Starting Backup Server 2..."
start_process "java -cp out BackupServer 2 6001" "BackupServer2"

echo "Starting Backup Server 3..."
start_process "java -cp out BackupServer 3 6002" "BackupServer3"

echo "Starting Admin Interface..."
start_process "java -cp out AdminInterface" "AdminInterface"

echo "All components started!"



