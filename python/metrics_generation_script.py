import sys
import metrics_generation as mg

Ar = eval(sys.argv[1])
SRm = eval(sys.argv[2])
count = int(sys.argv[3])
n = int(sys.argv[4])
y0 = mg.generate_metrics(Ar, SRm, count, n)

print(y0.tolist())