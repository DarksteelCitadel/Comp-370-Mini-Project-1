#!/bin/bash
# scripts/run.sh — start all components in separate macOS Terminal tabs and track PIDs

set -e

mkdir -p out logs
rm -f scripts/.pids

echo "Compiling Java files..."
javac src/*.java -d out || { echo "Compilation failed!"; exit 1; }

# Function to open a new Terminal tab and run a command
open_tab() {
  local name="$1"
  local cmd="$2"
  echo "Starting $name..."

  osascript <<EOF
tell application "Terminal"
    activate
    tell application "System Events" to keystroke "t" using command down
    delay 0.8
    do script "cd '$(pwd)'; $cmd" in front window
end tell
EOF

  # Wait for the process to start and capture its PID
  local pid=""
  for i in {1..15}; do
    pid=$(pgrep -f "$cmd" | head -n 1)
    if [ -n "$pid" ]; then
      echo "$name:$pid" >> scripts/.pids
      echo "$name started (PID $pid)"
      return
    fi
    sleep 1
  done
  echo "⚠️  Could not detect PID for $name after waiting 15 seconds"
}

echo "Launching components..."
open_tab "Monitor" "java -cp out Monitor"
open_tab "PrimaryServer" "java -cp out PrimaryServer 1 6000"
open_tab "BackupServer2" "java -cp out BackupServer 2 6001"
open_tab "BackupServer3" "java -cp out BackupServer 3 6002"
open_tab "AdminInterface" "java -cp out AdminInterface"

echo "✅ All components started!"
echo "📁 PID file created at scripts/.pids"


