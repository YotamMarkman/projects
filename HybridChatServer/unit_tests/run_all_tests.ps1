# Run all unit tests for HybridChatServer
# PowerShell version for Windows

Write-Host "========================================"
Write-Host "   HybridChatServer Unit Tests"
Write-Host "========================================"
Write-Host ""

$Failed = 0

# Go to project root
Set-Location $PSScriptRoot\..

Write-Host "[1/4] Compiling Java tests..."
javac -encoding UTF-8 -d . unit_tests/*.java src/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "COMPILATION FAILED!" -ForegroundColor Red
    exit 1
}
Write-Host "Compilation successful!" -ForegroundColor Green
Write-Host ""

Write-Host "[2/4] Running Protocol tests (Java)..."
Write-Host "----------------------------------------"
java unit_tests.ProtocolTest
if ($LASTEXITCODE -ne 0) {
    $Failed = 1
}
Write-Host ""

Write-Host "[3/4] Running ClientManager tests (Java)..."
Write-Host "----------------------------------------"
java unit_tests.ClientManagerTest
if ($LASTEXITCODE -ne 0) {
    $Failed = 1
}
Write-Host ""

Write-Host "[4/4] Running Python tests..."
Write-Host "----------------------------------------"
Set-Location unit_tests
python test_protocol.py
if ($LASTEXITCODE -ne 0) {
    $Failed = 1
}

python test_cross_language.py
if ($LASTEXITCODE -ne 0) {
    $Failed = 1
}
Set-Location ..
Write-Host ""

Write-Host "========================================"
if ($Failed -eq 0) {
    Write-Host "   ALL TESTS PASSED!" -ForegroundColor Green
} else {
    Write-Host "   SOME TESTS FAILED!" -ForegroundColor Red
}
Write-Host "========================================"

exit $Failed
