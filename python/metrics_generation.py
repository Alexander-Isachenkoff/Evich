import numpy as np


def generate_metrics(Ar, SRm, count, n):
    metrics = np.zeros((n, count))
    for j in range(0, n):
        Y0 = get_AR1(Ar[j], SRm[j], 2 * count)
        metrics[j] = Y0[count-1:-1]
    return metrics


def get_AR1(fi, sigma, count):
    mu = 0
    A = np.random.normal(mu, sigma, count)
    R = np.zeros(count)
    for j in range(0, count - 1):
        R[j + 1] = R[j] * fi + A[j]
    return R
