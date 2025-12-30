@echo off
setlocal

set "BASE_DIR=%cd%"

mkdir input 2>nul
mkdir output_generator 2>nul
mkdir output_solver 2>nul

echo Kompilacja solvera
cd ..\cuda-gauss-jordan-solver
gnumake
cd "%BASE_DIR%"

for %%i in (5 10 20 50 100 200 400 800) do (
    echo.
    echo ----------------------
    echo Testowanie dla N = %%i
    echo ----------------------

    echo [1/3] Generowanie...
    java -cp .\checker_generator\MatrixTW-1.0-SNAPSHOT-jar-with-dependencies.jar pl.edu.agh.macwozni.matrixtw.Generator %%i ".\input\in%%i.txt" ".\output_generator\out%%i.txt"

    echo [2/3] Liczenie na GPU...
    cd ..\cuda-gauss-jordan-solver
    gnumake run ARGS="%BASE_DIR%\input\in%%i.txt %BASE_DIR%\output_solver\out%%i.txt"
    cd "%BASE_DIR%"

    echo [3/3] Sprawdzanie...
    java -cp .\checker_generator\MatrixTW-1.0-SNAPSHOT-jar-with-dependencies.jar pl.edu.agh.macwozni.matrixtw.Checker ".\input\in%%i.txt" ".\output_solver\out%%i.txt"
)
echo
echo Sprzatanie po kompilacji solvera
gnumake clean

echo.
echo Koniec testow.
