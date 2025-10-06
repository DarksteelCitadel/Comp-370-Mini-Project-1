#!/bin/bash
# Top-level script to start monitor, servers, and admin client

# Make scripts executable
chmod +x *.sh

# Compile Java files
echo "Compiling Java files..."
javac -d out src/*.java

# Function to open a new Terminal tab and run a command
open_tab() {
  osascript <<EOF
tell application "Terminal"
    activate
    tell window 1
        do script "$1"
    end tell
end tell
EOF
}

echo "Starting Monitor..."
open_tab "cd $(pwd) && java -cp out Monitor"

echo "Starting Primary Server..."
open_tab "cd $(pwd) && java -cp out Main primary"

echo "Starting Backup Server 1..."
open_tab "cd $(pwd) && java -cp out Main backup1"

echo "Starting Backup Server 2..."
open_tab "cd $(pwd) && java -cp out Main backup2"

echo "Starting Admin Interface..."
open_tab "cd $(pwd) && java -cp out Main admin"

echo "All components started!"


echo "Starting Admin Interface..."
open_tab "cd $(pwd) && java -cp out Main admin"

echo "All components started!"
