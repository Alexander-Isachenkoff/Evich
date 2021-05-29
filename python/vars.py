import sys
import numpy as np

with open(sys.argv[1], "r") as my_file:
    str = my_file.read()

arr = eval(str)

n = len(arr)
vars = np.zeros(n)
for i in range(n):
    vars[i] = np.var(arr[i])

print(vars.tolist())