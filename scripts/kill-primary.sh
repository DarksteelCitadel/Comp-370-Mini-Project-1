#!/bin/bash
echo "Simulating Primary Server failure..."
# Since Main stops Primary automatically after a delay, this is optional
# You can still manually kill the Main process if needed
pkill -f "Main"
echo "Primary (and whole system) stopped."
