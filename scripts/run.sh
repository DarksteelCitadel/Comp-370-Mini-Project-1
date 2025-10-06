#!/bin/bash

mkdir -p out logs
rm -f scripts/.pids

echo "Compiling Java files..."
javac src/*.java -d out || { echo "Compilation failed!"; exit 1; }

# Function to start a process in a new tab and record PID
open_tab_and_record_pid() {
  local name="$1"
  local cmd="$2"
  osascript <<EOF
tell application "Terminal"
  activate
  tell application "System Events" to keystroke "t" using command down
  delay 0.5
  do script "cd '$(pwd)'; $cmd" in front window
end tell
EOF
  # Wait a bit and grab the PID
  sleep 1
  PID=$(pgrep -f "$cmd" | head -n 1)
  echo "$name:$PID" >> scripts/.pids
  echo "Started $name (PID $PID)"
}

open_tab_and_record_pid "Monitor" "java -cp out Monitor"
open_tab_and_record_pid "PrimaryServer" "java -cp out PrimaryServer 1 6000"
open_tab_and_record_pid "BackupServer2" "java -cp out BackupServer 2 6001"
open_tab_and_record_pid "BackupServer3" "java -cp out BackupServer 3 6002"
open_tab_and_record_pid "AdminInterface" "java -cp out AdminInterface"

echo "All components started! PIDs saved in scripts/.pids"

