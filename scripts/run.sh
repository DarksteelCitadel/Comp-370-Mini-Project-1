#!/bin/bash

# Create output folder
mkdir -p out

# Compile all Java files
echo "Compiling Java files..."
javac src/*.java -d out

# Run the whole system from Main.java
echo "Starting system (Monitor, PrimaryServer, BackupServers, AdminInterface)..."
java -cp out Main
