#!/bin/bash

# scripts/run.sh
mkdir -p out logs
rm -f scripts/.pids

echo "Compiling Java files..."
javac src/*.java -d out || { echo "Compilation failed!"; exit 1; }

record_pid() {
  local name="$1"
  local pattern="$2"

  # Wait until the process appears
  for i in {1..10}; do
    PID=$(pgrep -f "$pattern" | head -n 1)
    if [ -n "$PID" ]; then
      echo "$name:$PID" >> scripts/.pids
      echo "$name started (PID $PID)"
      return
    fi
    sleep 1
  done

  echo "⚠️ Could not find PID for $name after waiting 10s"
}

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
}

open_tab "Monitor" "java -cp out Monitor"
sleep 2; record_pid "Monitor" "java -cp out Monitor"

open_tab "PrimaryServer" "java -cp out PrimaryServer 1 6000"
sleep 2; record_pid "PrimaryServer" "java -cp out PrimaryServer 1 6000"

open_tab "BackupServer2" "java -cp out BackupServer 2 6001"
sleep 2; record_pid "BackupServer2" "java -cp out BackupServer 2 6001"

open_tab "BackupServer3" "java -cp out BackupServer 3 6002"
sleep 2; record_pid "BackupServer3" "java -cp out BackupServer 3 6002"

open_tab "AdminInterface" "java -cp out AdminInterface"
sleep 2; record_pid "AdminInterface" "java -cp out AdminInterface"

echo "✅ All components started!"
echo "📁 PIDs saved in scripts/.pids"


