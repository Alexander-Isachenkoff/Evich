from scipy.stats import f
import numpy as np
import copy
import sys


def filter_noise(z, count):
    z0 = []
    n = len(z)
    for i in range(1, n):
        z0.append(sorted(z[i]))

    vy = []

    for i in range(0, n - 1):
        vy.append(del_noise(z0[i], count))

    # fixed.drawing.draw_graph(vy, "Выбросы", False, 3)

    restored_z = []
    for i in range(0, n - 1):
        restored_z.append(replace_noise(z[i + 1], vy[i], count))
    # fixed.drawing.draw_graph(restored_z, "Выбросы", False, 3)

    r = copy.deepcopy(z)

    for i in range(1, n):
        r[i] = restored_z[i - 1]
    # fixed.drawing.draw_graph(r, "r", False, 4)
    return r


def del_noise(x, k):
    d = np.var(x)
    R = [0 for _ in range(k - 2)]
    for j in range(1, k - 1):
        R[j - 1] = x[j]
    d1 = np.var(R)
    s1 = d / d1
    s2 = f.ppf(0.95, k - 1, k - 3)
    if s1 < s2:
        return x
    else:
        return del_noise(R, k - 2)


def replace_noise(x, y, count):
    m = np.mean(y)
    R = [0 for _ in range(count)]
    for i in range(count):
        f = 0
        for j in range(len(y)):
            if x[i] == y[j]:
                R[i] = x[i]
                y[j] = 777
                f = 1
                break
        if f == 0:
            if i == 0:
                R[i] = m
            else:
                R[i] = R[i - 1]
    return R


z = np.array(eval(sys.argv[1]))
print(filter_noise(z, z.shape[1]).tolist())
