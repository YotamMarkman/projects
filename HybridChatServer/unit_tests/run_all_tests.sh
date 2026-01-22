cd un#!/bin/bash

# Run all unit tests for HybridChatServer
# Works in Git Bash on Windows or any Unix shell

echo "========================================"
echo "   HybridChatServer Unit Tests"
echo "========================================"
echo ""

# Track overall status
FAILED=0

# Go to the project root
cd "$(dirname "$0")/.."

echo "[1/4] Compiling Java tests..."
javac -d . unit_tests/*.java src/*.java
if [ $? -ne 0 ]; then
    echo "COMPILATION FAILED!"
    exit 1
fi
echo "Compilation successful!"
echo ""

echo "[2/4] Running Protocol tests (Java)..."
echo "----------------------------------------"
java unit_tests.ProtocolTest
if [ $? -ne 0 ]; then
    FAILED=1
fi
echo ""

echo "[3/4] Running ClientManager tests (Java)..."
echo "----------------------------------------"
java unit_tests.ClientManagerTest
if [ $? -ne 0 ]; then
    FAILED=1
fi
echo ""

echo "[4/4] Running Python tests..."
echo "----------------------------------------"
cd unit_tests
python test_protocol.py
if [ $? -ne 0 ]; then
    FAILED=1
fi

python test_cross_language.py
if [ $? -ne 0 ]; then
    FAILED=1
fi
cd ..
echo ""

echo "========================================"
if [ $FAILED -eq 0 ]; then
    echo "   ALL TESTS PASSED!"
else
    echo "   SOME TESTS FAILED!"
fi
echo "========================================"

exit $FAILED
