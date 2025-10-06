#!/bin/bash

# Make scripts executable
chmod +x scripts/*.sh

# Compile all Java files in src into out/
echo "Compiling Java files..."
javac -d out src/*.java
if [ $? -ne 0 ]; then
  echo "Compilation failed. Exiting."
  exit 1
fi

# Function to open new Terminal tab and run command in current directory
open_tab() {
  local cmd=$1
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
open_tab "java -cp out Main primary"

echo "Starting Backup Server 1..."
open_tab "java -cp out Main backup1"

echo "Starting Backup Server 2..."
open_tab "java -cp out Main backup2"

echo "Starting Admin Interface..."
open_tab "java -cp out AdminInterface"

echo "All components started!"
