import numpy as np

# def P(M,i):
#     print(f"P {i+1}")
#     return M[i][i]
# def H(M,i,j,pivot):
#     print(f"H {i+1} {j+1}")
#     M[i][j]/=pivot
# def A(M,k,i):
#     print(f"A {k+1} {i+1}")
#     return M[k][i]
# def B(M,i,j,a):
#     print(f"B {i+1} {j+1}")
#     return a*M[i][j]
# def C(M,k,j,b):
#     print(f"C {k+1} {j+1}")
#     M[k][j] -=  b
def gauss_jordan_simple(A, b):
    n = len(A)
    for i in range(n):
        A[i].append(b[i])
    for i in range(n):        
        for j in range(n):
            if i == j:
                continue
             # Operacja A (Obliczenie mnożnika)
            a_ji = A[i][i] / A[j][i]
            
            for k in range(n + 1):
def gauss_jordan(A, b):
    n = len(A)
    # last_operation przechowuje ID ostatniej operacji modyfikującej daną komórkę
    # Inicjalizacja '_' oznacza stan początkowy
    last_operation = [['_'] * (n + 1) for _ in range(n)]
    dependency_graph = {}

    for i in range(n):
        A[i].append(b[i])

    # --- FAZA ELIMINACJI ---
    for i in range(n):        
        for j in range(n):
            if i == j:
                continue
            
            # Operacja A (Obliczenie mnożnika)
            # Zależności Z3 i Z4: Wymaga aktualnych wartości z kolumny i (pivot i element pod nim)
            a_ji = A[i][i] / A[j][i]
            node_a = f"A_{i},{j}"
            if node_a not in dependency_graph: dependency_graph[node_a] = []
            
            dependency_graph[node_a].append(last_operation[j][i]) # Z3
            dependency_graph[node_a].append(last_operation[i][i]) # Z4

            for k in range(n + 1):
                # Operacja B (Skalowanie)
                # Zależność Z1: Wymaga mnożnika A
                # Zależność danych: Wymaga aktualnej wartości A[j][k]
                A[j][k] = A[j][k] * a_ji
                node_b = f"B_{i},{j},{k}"
                if node_b not in dependency_graph: dependency_graph[node_b] = []
                
                dependency_graph[node_b].append(node_a)              # Z1
                dependency_graph[node_b].append(last_operation[j][k])
                last_operation[j][k] = node_b

                # Operacja C (Odejmowanie)
                # Zależność Z2: Wymaga wyniku operacji B
                # Zależność danych: Wymaga wartości z wiersza źródłowego A[i][k]
                A[j][k] = A[j][k] - A[i][k]
                node_c = f"C_{i},{j},{k}"
                if node_c not in dependency_graph: dependency_graph[node_c] = []
                
                dependency_graph[node_c].append(last_operation[i][k])
                dependency_graph[node_c].append(last_operation[j][k]) # Z2 (pośrednio przez last_op)
                last_operation[j][k] = node_c

    # --- FAZA NORMALIZACJI ---
    for i in range(n):
        # Operacja D (Obliczenie normalizatora)
        # Zależność Z6: Wymaga ukończonej eliminacji na przekątnej ORAZ wyniku
        d_i = 1.0 / A[i][i]
        node_d = f"D_{i}"
        if node_d not in dependency_graph: dependency_graph[node_d] = []
        
        dependency_graph[node_d].append(last_operation[i][i]) # Część Z6
        dependency_graph[node_d].append(last_operation[i][n]) # Część Z6 (POPRAWKA)

        # Iterujemy tylko po przekątnej [i] i wyniku [n], bo reszta to zera
        for k in [i, n]:
            # Operacja E (Normalizacja wiersza)
            # Zależność Z5: Wymaga współczynnika D
            # Zależność danych: Wymaga wartości do przeskalowania
            A[i][k] = A[i][k] * d_i
            node_e = f"E_{i},{k}"
            if node_e not in dependency_graph: dependency_graph[node_e] = []
            
            dependency_graph[node_e].append(node_d)               # Z5
            dependency_graph[node_e].append(last_operation[i][k]) # Dane (POPRAWKA)
            last_operation[i][k] = node_e
    print(dependency_graph)
    return A
def load_data(filename):
    with open(filename, 'r') as f:
        lines = f.readlines()
    
    n = int(lines[0].strip())
    A = []
    
    # Wczytanie macierzy A (linie od 1 do n)
    for i in range(1, n + 1):
        row = [float(x) for x in lines[i].split()]
        A.append(row)
        
    # Wczytanie wektora b (linia n+1)
    b = [float(x) for x in lines[n + 1].split()]
    
    return A, b
def bigger_test():
    print("----Duży test----")
    epsilon = 1e-6
    A_m,b = load_data('./in.txt')
    A_test,b_test = load_data('./out.txt')
    gauss_jordan(A_m, b)
    N=len(A_test)
    for i in range(N):
        for j in range(N):
            if abs(A_test[i][j]-A_m[i][j]) > epsilon:
                print('Nie prawidłowe')
                exit()
    print("Wszystko poszło okej") 
def small_test():
    print("----Mały test----")
    epsilon = 1e-6
    A_m = [
        [2,1,3],
        [4,3,8],
        [6,5,16]
    ]
    b= [6,15,27]
    expected_sol = np.linalg.solve(np.array(A_m),np.array(b))
    gauss_jordan(A_m,b)
    my_sol = [row[-1] for row in A_m]
    print(my_sol)
    print(expected_sol)
    for i in range(len(expected_sol)):
        if abs(expected_sol[i]-my_sol[i]) > epsilon:
            print("Coś poszło nie tak")
            return
    print("Wszystko poszło okej") 
def mini_test():
    print("----Mini test----")
    epsilon = 1e-6
    A_m = [
        [2,1],
        [4,3]
    ]
    b= [6,15]
    expected_sol = np.linalg.solve(np.array(A_m),np.array(b))
    gauss_jordan(A_m,b)
    my_sol = [row[-1] for row in A_m]
    print(my_sol)
    print(expected_sol)
    for i in range(len(expected_sol)):
        if abs(expected_sol[i]-my_sol[i]) > epsilon:
            print("Coś poszło nie tak")
            return
    print("Wszystko poszło okej") 
bigger_test()
small_test()
#mini_test()

        

