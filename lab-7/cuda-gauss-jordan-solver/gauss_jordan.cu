#include <iostream>
#include <fstream>
#include <string>
#include <utility>
#include <iomanip>
#include <cuda_runtime.h>

using namespace std;
struct MatrixData {
    int N;      
    double* A;
};

MatrixData readInputMatrixFile(const char* path) {
    ifstream fileReader;
    fileReader.open(path);
    int N;

    if (!fileReader.is_open()) {
        cerr << "Nie mozna otworzyc pliku: " << path << endl;
        exit(1);
    }

    if (!(fileReader >> N)) {
        cerr << "Plik jest pusty lub nie zawiera rozmiaru N!" << endl;
        exit(1);
    }

    double* A = new double[N * (N + 1)];

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            if (!(fileReader >> A[i * (N + 1) + j])) {
                cerr << "[BLAD] Za malo danych w macierzy dla wiersza " << i << endl;
                exit(1);
            }
            if(abs(A[i * (N + 1) + j])<1e-11){
                A[i * (N + 1) + j]=0;
            }
        }
    }

    for (int i = 0; i < N; i++) {
        if (!(fileReader >> A[i * (N + 1) + N])) { 
            cerr << "[BLAD] Brak danych dla wektora b[" << i << "]" << endl;
            exit(1);
        }
         if(abs(A[i * (N + 1) + N])<1e-11){
                A[i * (N + 1) + N]=0;
            }
    }

    fileReader.close();
    return {N, A};
}


void writeOutputToMatrixFile(const char* path, int N, const double* A) {
    ofstream fileWriter;
    fileWriter.open(path);

    if (!fileWriter.is_open()) {
        cerr << "BLAD: Nie mozna utworzyc pliku: " << path << endl;
        return;
    }

    fileWriter << N << endl;

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            fileWriter << setprecision(1) << fixed << abs(A[i * (N + 1) + j]) << " ";
        }
        fileWriter << endl;
    }

    for (int i = 0; i < N; i++) {
        fileWriter << setprecision(10) << fixed <<  A[i * (N + 1) + N] << " ";
    }
    fileWriter << endl;

    fileWriter.close();
    cout << "Zapisano dane do pliku: " << path << endl;
}


#define IDX(row, col, N) ((row) * ((N) + 1) + (col))

__global__ void operation_A(double *A, double *a_ji, int i, int N) {
    int j = blockIdx.x * blockDim.x + threadIdx.x; 

    if (j < N && j != i) {
         if (abs(A[IDX(j, i, N)]) > 1e-20) {
            a_ji[j] = A[IDX(j, i, N)] / A[IDX(i, i, N)];
         } else {
            a_ji[j] = 0.0;
         } 
    }
}

__global__ void operation_BC(double *A, double *a_ji, int i, int N) {
    int j = blockIdx.y * blockDim.y + threadIdx.y; 
    int k = blockIdx.x * blockDim.x + threadIdx.x; 

    if (j < N && k < N + 1 && j != i) {
        if (abs(a_ji[j]) > 1e-20) {
            A[IDX(j, k, N)] = A[IDX(j, k, N)] - A[IDX(i, k, N)] * a_ji[j];
        }
    }
}

__global__ void clean_pivot_column(double *A, int i, int N) {
    int j = blockIdx.x * blockDim.x + threadIdx.x; // Wiersz

    if (j < N && j != i) {
        A[IDX(j, i, N)] = 0.0;
    }
}

__global__ void operation_D(double *A, double* d_i, int N) {
    int i = blockDim.x * blockIdx.x + threadIdx.x;
    if (i < N) {
        if (abs(A[IDX(i,i,N)]) > 1e-20)
            d_i[i] = 1.0 / A[IDX(i,i,N)];
        else
            d_i[i] = 1.0;
    }
}

__global__ void operation_E(double *A,  double* d_i, int N) {
    int i = blockDim.y * blockIdx.y + threadIdx.y;
    int k = blockIdx.x * blockDim.x + threadIdx.x;
    
    if (i < N && k < N +1)
        A[IDX(i,k,N)] = A[IDX(i,k,N)] *  d_i[i];
}

// Wektor wyrazow wolnych b doklejony do macierzy
void gauss_jordan(double * A, int N){

    double *d_A,*d_multipliers;

    //Alokowanie dany na GPU
    cudaMalloc(&d_A, sizeof(double)*N*(N+1));
    cudaMalloc(&d_multipliers, sizeof(double)*N);

    //Kopiowanie danych z CPU na GP
    cudaMemcpy(d_A, A, sizeof(double)*N*(N+1), cudaMemcpyHostToDevice);

    //Uruchamianie kernela
    //Konfiguracja kerneli dla eleminiacji i normalizacji
    dim3 threadsPerBlock2D(16, 16);
    dim3 blocksPerGrid2D(
        (N + 1 + threadsPerBlock2D.x - 1) / threadsPerBlock2D.x,
        (N + threadsPerBlock2D.y - 1) / threadsPerBlock2D.y
    );

    //Konfiguracja kerneli 1d dla mnożników
    int threadsPerBlock1D = 256;
    int blocksPerGrid1D = (N + threadsPerBlock1D - 1) / threadsPerBlock1D;

    for(int i=0;i<N;i++){
        operation_A<<<blocksPerGrid1D, threadsPerBlock1D>>>(d_A,d_multipliers,i,N);
        cudaDeviceSynchronize();

        operation_BC<<<blocksPerGrid2D, threadsPerBlock2D>>>(d_A,d_multipliers,i,N);
        cudaDeviceSynchronize();

        clean_pivot_column<<<blocksPerGrid1D, threadsPerBlock1D>>>(d_A,i,N);
        cudaDeviceSynchronize();

    }

    operation_D<<<blocksPerGrid1D, threadsPerBlock1D>>>(d_A,d_multipliers,N);
    cudaDeviceSynchronize();

    operation_E<<<blocksPerGrid2D, threadsPerBlock2D>>>(d_A,d_multipliers,N);
    cudaDeviceSynchronize();

    cudaMemcpy(A, d_A, sizeof(double)*N*(N+1), cudaMemcpyDeviceToHost);

    cudaFree(d_A); cudaFree(d_multipliers);

} 
int main(int argc, char *argv[]) {

    if(argc!=3){
        cerr << "BLAD: Podano nieprawidlow ilosc argumento. Wymagane dwie sciezki "<< endl;
        return -1;
    }
    const char* inputPath = argv[1];
    const char* outputPath = argv[2];

    MatrixData data = readInputMatrixFile(inputPath);
    
    gauss_jordan(data.A, data.N);

    writeOutputToMatrixFile(outputPath, data.N, data.A);

    return 0;
}