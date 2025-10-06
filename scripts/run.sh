#!/bin/bash
# scripts/run.sh
echo "Compiling Java files..."
mkdir -p ../out
javac ../src/*.java -d ../out

echo "Starting all components..."

# Start Monitor
osascript -e 'tell app "Terminal" to do script "cd ~/Comp-370-Mini-Project-1 && java -cp out MonitorMain"'
sleep 1

# Start Primary Server
osascript -e 'tell app "Terminal" to do script "cd ~/Comp-370-Mini-Project-1 && java -cp out Main primary"'
sleep 1

# Start Backup 1
osascript -e 'tell app "Terminal" to do script "cd ~/Comp-370-Mini-Project-1 && java -cp out Main backup1"'
sleep 1

# Start Backup 2
osascript -e 'tell app "Terminal" to do script "cd ~/Comp-370-Mini-Project-1 && java -cp out Main backup2"'
sleep 1

# Start Admin Interface
osascript -e 'tell app "Terminal" to do script "cd ~/Comp-370-Mini-Project-1 && java -cp out AdminInterfaceMain"'
sleep 1

echo "All components started!"
