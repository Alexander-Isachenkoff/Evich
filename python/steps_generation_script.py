import sys
import steps_generation as stg

n = int(sys.argv[1])
count = int(sys.argv[2])
M_S = eval(sys.argv[3])
A_S = eval(sys.argv[4])

steps = stg.generate_steps(n, count, M_S, A_S)

print(steps.tolist())