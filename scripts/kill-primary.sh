#!/bin/bash
echo "Killing the Primary Server (ID 1)..."
# This works if the original PrimaryServer thread is still running
pkill -f "PrimaryServer 1 6000"
