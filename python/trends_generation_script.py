import sys
import trends_generation as tr

n = int(sys.argv[1])
count = int(sys.argv[2])
RegC = eval(sys.argv[3])

trends = tr.generate_trends(count, n, RegC)

print(trends)