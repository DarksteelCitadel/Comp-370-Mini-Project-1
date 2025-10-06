#!/bin/bash

# Compile Java files
echo "Compiling Java files..."
mkdir -p out
javac src/*.java -d out

# Run the Main program which starts everything
echo "Starting the SRMS simulation..."
java -cp out Main


