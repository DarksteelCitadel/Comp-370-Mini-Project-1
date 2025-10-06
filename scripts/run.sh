#!/bin/bash

# Compile Java files
echo "Compiling Java files..."
javac src/*.java -d out

mkdir -p out

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

echo "Starting Monitor..."
open_tab "java -cp out Monitor"

echo "Starting Primary Server..."
open_tab "java -cp out PrimaryServer 1 6000"

echo "Starting Backup Server 2..."
open_tab "java -cp out BackupServer 2 6001"

echo "Starting Backup Server 3..."
open_tab "java -cp out BackupServer 3 6002"

echo "Starting Admin Interface..."
open_tab "java -cp out AdminInterface"

echo "All components started!"
