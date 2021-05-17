import sys
import steps_filtration as stf
import numpy as np

YnLin = np.array(eval(sys.argv[1]))
n = len(YnLin)
count = len(YnLin[0])
y1lin, y2lin, y_a_b = np.zeros((n, count)), np.zeros((n, count)), np.zeros((n, 2))
for j in range(n):
    y1lin[j] = stf.reg_lin(YnLin[j], count)
    y2lin[j] = stf.reg_sq(YnLin[j])
    y_a_b[j] = stf.reg_a_b(YnLin[j])

Yres_lin = np.zeros((n, count))
for j in range(n):
    Yres_lin[j] = stf.res_Fish(YnLin[j], y1lin[j], y2lin[j], count)

print(y1lin.tolist())
print(y2lin.tolist())
print(y_a_b.tolist())
print(Yres_lin.tolist())