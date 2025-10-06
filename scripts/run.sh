#!/bin/bash

# Compile Java files
echo "Compiling Java files..."
mkdir -p out
javac src/*.java -d out

# Run the system (all servers + monitor + admin + client) via Main
echo "Starting all components..."
java -cp out Main
