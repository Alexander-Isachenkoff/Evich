import numpy as np


def real_step(Mi, Ai, count):
    jk = 0
    R = np.zeros(count)
    for k in range(0, len(Mi)):
        if Mi[k] > 0:
            for j in range(jk, Mi[k]):
                R[j] = Ai[k]
            jk = Mi[k]
        else:
            if jk < count - 1:
                for j in range(jk, count):
                    R[j] = Ai[k]
                jk = j
    return R


def generate_steps(n, count, M_S, A_S):
    ST = np.zeros((n, count))
    for j in range(0, n):
        Mj = np.array(M_S)[:, j]
        Aj = np.array(A_S)[:, j]
        ST[j] = real_step(Mj, Aj, count)
    return ST
