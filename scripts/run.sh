#!/bin/bash

# Compile Java files
echo "Compiling Java files..."
mkdir -p out
javac src/*.java -d out

# Run the Main program
echo "Starting the entire system via Main..."
java -cp out Main
