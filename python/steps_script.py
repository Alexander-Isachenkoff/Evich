import sys
import steps_filtration as stf
import numpy as np

z_filtered_noise = np.array(eval(sys.argv[1]))
Ks = int(sys.argv[2])
found_steps, Z_no_lin, YnLin, moments, ampl = stf.filter_steps2(z_filtered_noise, Ks)
print(found_steps.tolist())
print(Z_no_lin.tolist())
print(YnLin.tolist())
print(moments)
print(ampl.tolist())