#!/bin/bash

# Clear previous compiled files
rm -rf out/*
mkdir -p out

# Compile all Java files
javac -d out src/*.java

# Start Monitor in background
echo "Starting Monitor..."
java -cp out Monitor &
sleep 1  # give it a second to start

# Start PrimaryServer
echo "Starting PrimaryServer..."
java -cp out PrimaryServer 1 6000 &
sleep 1

# Start BackupServers
echo "Starting BackupServer 2..."
java -cp out BackupServer 2 6001 &
sleep 1

echo "Starting BackupServer 3..."
java -cp out BackupServer 3 6002 &
sleep 1

# Start Admin Interface
echo "Starting Admin Interface..."
java -cp out AdminInterface




