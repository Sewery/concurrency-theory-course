import re

def render_graph(path,FNF:str,digraph:list[list[int]],word:str):
    '''
    Render the dependency digraph using Graphviz.
    '''
    from graphviz import Digraph
    from graphviz.backend.execute import ExecutableNotFound
    import os

    n = len(word)
    if len(digraph) < n + 1:
        digraph = digraph + [[] for _ in range(n + 1 - len(digraph))]

    dot = Digraph('g')
    dot.attr('node', shape='circle')

    for i in range(1, n + 1):
        dot.node(str(i), label=word[i - 1])

    for u in range(1, n + 1):
        for v in sorted(digraph[u]):
            if 1 <= v <= n:
                dot.edge(str(u), str(v))

    base, ext = os.path.splitext(path)
    dot_path = (base or path) + '.dot'
    with open(dot_path, 'w', encoding='utf-8') as f:
        f.write(dot.source)
    print(f' Saved DOT file to {dot_path}')

    try:
        from graphviz import Source
        src = Source.from_file(dot_path)
        fmt = ext.lstrip('.').lower() or 'png'
        src.format = fmt
        out = src.render(filename=(base or path), cleanup=True)
        print(f' Rendered graph to {out}')
    except ExecutableNotFound:
        print(' Graphviz "dot" not found. Skipping image rendering.')
def read_data_from_txt(path:str)->tuple[list[list],list[str],str]:
    '''
    Read data for task from file.
    Returns (transactions, alphabet, word)
    transactions: [[letter, assigned_var, [depends...]], ...]
    '''
    transactions: list[list] = []
    A: list[str] = []
    w: str = ""

    with open(path, encoding="utf-8") as f:
        for raw in f:
            line = raw.strip()
            if not line:
                continue

            # Transaction line e.g.: (a) x := x + 1
            m = re.match(r'^\((\w)\)\s*([A-Za-z])\s*:=\s*(.+)$', line)
            if m:
                letter = m.group(1)
                left_var = m.group(2)
                rhs = m.group(3)

                # Collect tokens like x, v, z, x
                deps = re.findall(r'[a-z]', rhs)
                # Keep order, remove duplicates
                deps = list(dict.fromkeys(deps))
                transactions.append([letter, left_var, deps])
                continue

            # Alphabet line e.g.: A = {a, b, c}
            if line.startswith('A ='):
                brace = re.search(r'\{(.+)\}', line)
                if brace:
                    A = [t.strip() for t in brace.group(1).split(',') if t.strip()]
                continue

            # Word line e.g.: w = acdcfbbe
            if line.startswith('w ='):
                w = line.split('=', 1)[1].strip()
                continue
                
    return transactions, A, w
def write_data_to_txt(path:str,D:list[tuple[str,str]],I:list[tuple[str,str]],FNF:str,digraph:list[list[int]],word:str):
    '''
    Write solution of scheduling threads to file 
    '''
    def fmt_rel(rel: list[tuple[str, str]]) -> str:
        if not rel:
            return '{}'
        items = sorted(set(rel))
        inner = ', '.join(f'({a}, {b})' for a, b in items)
        return '{' + inner + '}'

    lines: list[str] = []
    lines.append(f'D = {fmt_rel(D)}')
    lines.append(f'I = {fmt_rel(I)}')
    lines.append(f'FNF([w]) = {FNF}')
    lines.append('digraph g{')

    n = max(0, len(digraph) - 1)
    # edges (sorted for stable output)
    for u in range(1, n + 1):
        for v in sorted(digraph[u]):
            lines.append(f'{u} -> {v}')

    for i in range(1, n + 1):
        lines.append(f'{i}[ label={word[i-1]} ]')

    lines.append('}')

    with open(path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

def get_graph(word,I,D):
    n = len(word)
    digraph = [[] for _ in range(n+1)]

    for i in range(n):
        for j in range(i + 1, n):
            if (word[i], word[j]) in D:
                digraph[i + 1].append(j + 1)

    def is_edge_replacable(i,j,curr):
        if curr==j:
            return True
        for e in digraph[curr]:
            if curr==i and e==j:
                continue
            if is_edge_replacable(i,j,e):
                return True
        return False
                
    for i in range(1,len(digraph)):
        to_remove = []
        for j in digraph[i]:
            if is_edge_replacable(i, j, i):
                to_remove.append(j)
        if to_remove:
            digraph[i] = [v for v in digraph[i] if v not in to_remove]
    return digraph

def get_realtions_from_transactions(transactions):
    D = set()
    I = set()
    for letter, var, depends in transactions:
        for other_letter, other_var, other_depends in transactions:
            if other_letter == letter:
                D.add((letter, letter))
                continue
            if var in other_depends or other_var in depends:
                D.add((letter, other_letter))
            else:
                I.add((letter, other_letter))
    D_list = sorted(D)
    I_list = sorted(I)
    return D_list, I_list

def get_FNF(word,digraph)->str:
    n = len(word)
    if len(digraph) < n + 1:
        digraph = digraph + [[] for _ in range(n + 1 - len(digraph))]

    indeg = [0] * (n + 1)
    for u in range(1, min(len(digraph), n + 1)):
        for v in digraph[u]:
            if 1 <= v <= n:
                indeg[v] += 1

    remaining = set(range(1, n + 1))
    factors = []
    while remaining:
        layer = sorted(i for i in remaining if indeg[i] == 0)
        if not layer:
            i = min(remaining)
            layer = [i]

        letters = sorted((word[i - 1] for i in layer))
        factors.append('(' + ''.join(letters) + ')')

        for u in layer:
            for v in digraph[u] if u < len(digraph) else []:
                if 1 <= v <= n:
                    indeg[v] -= 1
            remaining.remove(u)

    return ''.join(factors)

def test_read_data_from_txt():
    path ='./test_data/case1.txt'

    transactions, A, w = read_data_from_txt(path)

    transactions_test = [
        ['a','x',['x']],         # (a) x := x + 1
        ['b','y',['y','z']],    # (b) y := y + 2z
        ['c','x',['x','z']],    # (c) x := 3x + z
        ['d','w',['w','v']],     # (d) w := w + v
        ['e','z',['y','z']],     # (e) z := y - z
        ['f','v',['x','v']],     # (f) v := x + v
    ]
    A_test = ['a','b','c','d','e','f']
    w_test = 'acdcfbbe'

    print('Result A:',A)
    print('Result transactions:',transactions)
    print('Result w:',w)
    assert A_test == A
    assert transactions_test == transactions
    assert w_test == w

def test_write_data_to_txt():
    w_test_1 = 'baadcb'
    D_test_1 = [('a','a'), ('a','b'), ('a','c'), ('b','a'), ('b','b'), ('b','d'),
        ('c','a'), ('c','c'), ('c','d'), ('d','b'), ('d','c'), ('d','d')]
    I_test_1 = [('a','d'), ('d','a'), ('b','c'), ('c','b')]
    digraph_test_1 =[[],[2,4],[3],[5,6],[5,6],[],[]]
    fnf_test_1 = '(b)(ad)(a)(bc)'
    write_data_to_txt('./test_data/case_test_out.txt',D_test_1,I_test_1,fnf_test_1,digraph_test_1,w_test_1)

def test_data_set_1():
    transactions_test_1 = [
        ['a','x',['x','y']],
        ['b','y',['y','z']],
        ['c','x',['x','z']],
        ['d','z',['y','z']],
    ]
    A_test_1 = ['a','b','c','d']
    w_test_1 = 'baadcb'
    D_test_1 = [('a','a'), ('a','b'), ('a','c'), ('b','a'), ('b','b'), ('b','d'),
        ('c','a'), ('c','c'), ('c','d'), ('d','b'), ('d','c'), ('d','d')]
    I_test_1 = [('a','d'), ('d','a'), ('b','c'), ('c','b')]
    digraph_test_1 =[[],[2,4],[3],[5,6],[5,6],[],[]]
    fnf_test_1 = '(b)(ad)(a)(bc)'

    # Test relations
    D, I = get_realtions_from_transactions(transactions_test_1)
    print('Result D:',D)
    print('Result I:',I)
    assert sorted(D_test_1) == D
    assert sorted(I_test_1) == I

    digraph = get_graph(w_test_1,I,D)
    print('Result digraph:',digraph)
    assert digraph_test_1 == digraph

    fnf = get_FNF(w_test_1,digraph)
    print('Result FNF:',fnf)
    assert fnf_test_1 == fnf

# Testing
test_read_data_from_txt()   
test_write_data_to_txt() 
test_data_set_1()

def solve(read_path,write_path,graph_path):
    transactions, A, word = read_data_from_txt(read_path)

    D, I = get_realtions_from_transactions(transactions)
    digraph = get_graph(word,I,D)
    FNF = get_FNF(word,digraph)
    render_graph(graph_path,FNF,digraph,word)

    write_data_to_txt(write_path,D,I,FNF,digraph,word)

# Solving cases in test_data

solve('./test_data/case1.txt','./test_data/case1_out.txt','./test_data/case1_graph.png')
solve('./test_data/case2.txt','./test_data/case2_out.txt','./test_data/case2_graph.png')

