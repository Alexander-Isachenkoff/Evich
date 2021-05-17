import math
from sklearn.linear_model import LinearRegression
import numpy as np
from scipy.stats import f
from steps_generation import real_step


def filter_steps(Z, Ks, A_S, M_S):
    n = Z.shape[0]
    count = Z.shape[1]

    steps_moments = find_steps(Z, Ks)
    #print('Моменты:\n', steps_moments)

    ISt = IndSt(steps_moments, n, count)

    expected_steps = []
    for j in range(n):
        expected_steps.append(real_step(np.transpose(M_S)[j], np.transpose(A_S)[j], count))
    expected_steps = np.transpose(expected_steps)

    Alin = [AmplOpt(Z[j], steps_moments[j], expected_steps[0][j], 1, count) for j in range(n)]
    Alin = np.transpose(Alin)

    #print('Alin\n', Alin)

    SOpAll = StOp(ISt, steps_moments, Alin, count, n)
    SOplin = SOpAll[0]
    ISt = SOpAll[1]

    found_steps = StNoOp(ISt, steps_moments, np.array(Alin), count, n)
    found_steps = np.transpose(found_steps)
    found_steps[0] = SOplin

    # Удаляем ступенчатые функции
    Z_no_lin = np.zeros((n, count))
    for i in range(1, len(Z)):
        Z_no_lin[i] = Z[i] + found_steps[i] - found_steps[0] # + trends[i] - trends[0]

    # Z_test = Z_no_lin - z0
    # fixed.drawing.draw_graph(Z_test, 'test', False, 4)

    # Z_no_lin = z0

    YnLin = np.zeros((n, count))
    sum_z = np.zeros(count)
    for z in Z_no_lin:
        sum_z += z
    YnLin[0] = sum_z / n

    for j in range(1, n):
        YnLin[j] = YnLin[0] - Z_no_lin[j]

    return expected_steps, found_steps, Z_no_lin, YnLin

def filter_steps2(Z, Ks):
    n = Z.shape[0]
    count = Z.shape[1]

    steps_moments = find_steps(Z, Ks)

    ISt = IndSt(steps_moments, n, count)

    Alin = [AmplOpt(Z[j], steps_moments[j], Z[0][j], 1, count) for j in range(n)]
    Alin = np.transpose(Alin)

    SOpAll = StOp(ISt, steps_moments, Alin, count, n)
    SOplin = SOpAll[0]
    ISt = SOpAll[1]

    found_steps = StNoOp(ISt, steps_moments, np.array(Alin), count, n)
    found_steps = np.transpose(found_steps)
    found_steps[0] = SOplin

    # Удаляем ступенчатые функции
    Z_no_lin = np.zeros((n, count))
    for i in range(1, len(Z)):
        Z_no_lin[i] = Z[i] + found_steps[i] - found_steps[0] # + trends[i] - trends[0]

    YnLin = np.zeros((n, count))
    sum_z = np.zeros(count)
    for z in Z_no_lin:
        sum_z += z
    YnLin[0] = sum_z / n

    for j in range(1, n):
        YnLin[j] = YnLin[0] - Z_no_lin[j]

    return found_steps, Z_no_lin, YnLin

def find_steps(Z, Ks):
    M = []
    max_len = 0
    for j in range(0, len(Z)):
        t = find_step_moments(Z[j], Ks, len(Z[j]))
        if len(t) > max_len:
            max_len = len(t)
        M.append(t)

    for row in M:
        row.extend([0 for _ in range(max_len - len(row))])
    return M


# Определить моменты скачков
def find_step_moments(X, k, count):
    """Stup_Mom"""
    z = [0 for _ in range(count)]
    for j in range(1, count):
        z[j - 1] = X[j] - X[j - 1]
    median = np.median(z)
    R = [0 for _ in range(count - 1)]
    for j in range(count - 1):
        R[j] = abs(z[j] - median)
    median = np.median(R)
    cko = median / 0.6745
    i = 0
    M = [0 for _ in range(count - 1)]
    for j in range(count - 1):
        if abs(z[j]) > cko * k:
            M[i] = j + 1
            i = i + 1
    return M[0:i + 1]


def res_i(count):
    return [i for i in range(count)]


def Reg(a, b, x):
    R = a + b * x
    return R


def reg_a_b(Y):
    I = np.array(res_i(len(Y))).reshape(-1, 1)
    model = LinearRegression()
    model.fit(I, Y)
    R = [0 for _ in range(2)]
    R[0] = model.intercept_
    R[1] = model.coef_
    return R


def Agold(X, m0, m, cnt): # ..., 0, 2, 300
    r = X[m] - X[m - 1]
    ax = r - 3
    bx = r + 3
    fi = (3 - math.sqrt(5)) / 2
    x = [0, 0]
    while abs(bx - ax) > 0.00001:
        x[0] = ax + (bx - ax) * fi
        x[1] = ax + bx - x[0]
        S = [0, 0]
        for i in range(2):
            z = np.zeros(cnt - m0)
            j = 0
            for k in range(m0, cnt):
                if k >= m:
                    z[j] = X[k] - x[i]
                else:
                    z[j] = X[k]
                j += 1
            I = np.array(res_i(j)).reshape(-1, 1)
            model = LinearRegression()
            model.fit(I, z)
            a = model.intercept_
            b = model.coef_
            ztr = Reg(a, b, I)
            for k in range(j-1):
                S[i] += (z[k] - ztr[k]) ** 2
        if S[1] < S[0]:
            ax = x[0]
        else:
            bx = x[1]
        R = (ax + bx) / 2
    return R


# Определить генератор, в котором был скачок
def IndSt(M, n, count):
    R = [[0 for j in range(n)] for i in range(count)]
    k = len(M[0])
    M = np.transpose(M)
    for ik in range(0, count):
        for kj in range(1, n):
            R[ik][kj] = 0
            for ki in range(0, k):
                # print('ik ', ik, 'ki ', ki, 'kj ', kj)
                if (ik == M[ki][kj]) and (M[ki][kj] > 0):
                    R[ik][kj] = 1
    return R


"""#Вычисляем амплитуду скачков """


# Оценить среднее значение м/у соседними скачками
def AmplOpt(X, M, A0, flag, count):
    R = [0 for _ in range(len(M))]
    R[0] = A0
    k = 0
    while M[k] > 0:
        if k == 0:
            m0 = 0
        else:
            m0 = M[k - 1]
        if M[k + 1] > 0:
            cnt = M[k + 1] - 1
        else:
            cnt = count
        if flag == 1:
            R[k + 1] = -Agold(X, m0, M[k], cnt)
        # else:
        #     R[k + 1] = -Agold2(X, m0, M[k], cnt)
        k = k + 1
    return R


# Вычислить амплитуду опорного генератора
def StOp(I, M, A, count, n):
    St = np.zeros(count) + A[0][0]
    AS = np.zeros(n)
    k = len(M[0])
    M = np.transpose(M)
    # for i in range(count):
    #     St[i] = A[0][0]
    for i in range(count):
        b = True
        for Ii in I[i]:
            if Ii != 1:
                b = False
        if b:
            I[i][0] = 1
            summa = 0
            for kj in range(1, n):
                for ki in range(k):
                    if (i == M[ki][kj]) and (M[ki][kj] > 0):
                        # while len(AS) < kj + 1:
                        #     AS.append(0)
                        if A[ki + 1][kj] == 0:
                            AS[kj] = -A[k + 1][kj]
                        else:
                            AS[kj] = -A[ki + 1][kj]
                        summa += AS[kj]
            av = summa / 3
            for ki in range(i, count):
                St[ki] = St[ki] + av
    return St, I


# Формирование ступенек для неопорного генератора

def StNoOp(I, M, A, count, n):
    k = len(M[1])
    M = np.transpose(M)
    AS = np.zeros(n)
    St = np.zeros((count, n)) + A[0]
    # St = [[0 for i in range(n)] for j in range(count)]
    # for i in range(count):
    #     for kj in range(1, n):
    #         St[i][kj] = A[0][kj]

    for i in range(count):
        for kj in range(1, n):
            if (I[i][kj] == 1) and (I[i][0] != 1):
                for ki in range(k):
                    if (i == M[ki][kj]) and (M[ki][kj] > 0):
                        if A[ki + 1][kj] == 0:
                            AS[kj] = A[k + 1][kj]
                        else:
                            AS[kj] = A[ki + 1][kj]
                av = AS[kj]
                for ki in range(i, count):
                    St[ki][kj] = St[ki][kj] + av
    # print(np.array(St).shape)
    return St

# Удалить наиболее значимый тренд (линейный / квадратичный)


def res_Fish(X, X1, X2, count):
    L = Fisher2(X, X1, X2, count)
    if L[0][2] == 0:
        R = X
    else:
        if L[2][2] == 1:
            R = X - X2
        else:
            R = X - X1
    return R


# Проверить значимость линейной и квадратичной регрессий

def Fisher2(X, X1, X2, count):
    M_X = np.mean(X)
    S1 = 0
    S2 = 0
    for i in range(len(X1)):
        S1 += (X1[i] - M_X) ** 2
        S2 += (X[i] - X1[i]) ** 2
    S2 = S2 / (count - 2)
    R = [[0 for _ in range(4)] for _ in range(4)]
    R[0][0] = S1 / S2
    R[0][1] = f.ppf(0.53, 1, count - 2)
    if R[0][0] > R[0][1]:
        R[0][2] = 1
    else:
        R[0][2] = 0
    S1 = 0
    S2_s = 0
    for i in range(len(X2)):
        S1 += (X1[i] - M_X) ** 2
        S2_s += (X[i] - X2[i]) ** 2
    S1 = S1 / 2
    S2_s = S2_s / (count - 3)
    R[1][0] = S1 / S2_s
    R[1][1] = f.ppf(0.53, 2, count - 3)
    if R[1][0] > R[1][1]:
        R[1][2] = 1
    else:
        R[1][2] = 0
    R[2][0] = S2 / S2_s
    R[3][1] = f.ppf(0.53, count - 2, count - 3)
    if R[2][0] > R[2][1]:
        R[2][2] = 1
    else:
        R[2][2] = 0
    return R


def reg_lin(Y, n):
    k = reg_a_b(Y)
    R = np.zeros(n)
    #print('k:', k)
    for i in range(n):
        R[i] = Reg(k[0], k[1], i)
    return R


def reg_sq(y):
    from sklearn.pipeline import Pipeline
    from sklearn.preprocessing import PolynomialFeatures
    from sklearn.linear_model import LinearRegression
    x = np.array([i for i in range(len(y))])
    Input = [('polynomial', PolynomialFeatures(degree=2)), ('modal', LinearRegression())]
    pipe = Pipeline(Input)
    pipe.fit(x.reshape(-1, 1), y.reshape(-1, 1))
    poly_pred = pipe.predict(x.reshape(-1, 1))
    return poly_pred.reshape(len(y))
