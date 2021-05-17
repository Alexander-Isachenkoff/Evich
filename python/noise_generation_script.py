import noise_generation as ng
import sys
import numpy as np

count = int(sys.argv[1])
n = int(sys.argv[2])
per = int(sys.argv[3])
sigma = float(sys.argv[4])
noise = ng.generate_noise(count, n, per, sigma)

print(noise.tolist())