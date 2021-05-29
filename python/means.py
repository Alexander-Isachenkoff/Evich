import sys
import numpy as np

with open(sys.argv[1], "r") as my_file:
    str = my_file.read()

arr = eval(str)

n = len(arr)
means = np.zeros(n)
for i in range(n):
    means[i] = np.mean(arr[i])

print(means.tolist())