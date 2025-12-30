ARGS =

all:
	nvcc --allow-unsupported-compiler gauss_jordan.cu -o example.exe -O3 -arch=sm_86
diagnostic-compile:
	nvcc --allow-unsupported-compiler diagnostic.cu -o diagnostic.exe -O3 -arch=sm_86
run:
	./example.exe $(ARGS)
diagnostic-run:
	./diagnostic.exe
clean:
	del example.exe example.lib example.exp diagnostic.exe diagnostic.lib diagnostic.exp