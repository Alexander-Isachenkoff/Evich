import numpy as np


def generate_noise(count, n, per, sigma):

    noise = np.zeros((n, count))

    for k in range(1, n):
        P = np.random.uniform(0, 100, count)
        noise[k] = np.random.normal(0, sigma, count)
        for j in range(count):
            if P[j] > per or j == 0:
                noise[k][j] = 0
    return noise

