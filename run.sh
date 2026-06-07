#!/usr/bin/env bash
# run.sh — build and run Dungo on Linux / macOS / WSL
#
# Usage:
#   chmod +x run.sh
#   ./run.sh

set -e

echo "=== Dungo — build & run ==="

# Compile
mkdir -p out
javac -d out src/com/hmimesh/game/*.java
echo "Compiled OK."

# Run
java -cp out com.hmimesh.game.Main
