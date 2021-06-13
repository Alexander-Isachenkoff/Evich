import sys
import numpy as np

str = [0,0]
with open(sys.argv[1], "r") as my_file:
    str[0] = my_file.readline()
    str[1] = my_file.readline()

y0 = np.array(eval(str[0]))
y  = np.array(eval(str[1]))

abs_error = y - y0
print(abs_error.tolist())