#!/bin/bash

echo "Killing the Primary Server (ID 1)..."

# This finds the Java process running Main that contains PrimaryServer and kills it
pkill -f "PrimaryServer 1 6000"
