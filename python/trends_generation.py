import numpy as np


def generate_trends(count, n, RegC):
    trends = []

    for j in range(0, n):
        trends.append(Reg1(count, RegC[0][j], RegC[1][j], RegC[2][j]))
    return trends


def Reg1(count, a, b, c):
    R = []
    for j in range(count):
        R.append(a + b * (j + 1) + c * (j + 1) ** 2)
    return R
