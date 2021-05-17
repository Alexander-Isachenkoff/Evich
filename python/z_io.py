import numpy as np


def read(n):
    Z = []
    for i in range(n):
        file = open('Gen/Zi_' + str(i + 1) + '.txt')
        Z.append(np.array([float(line.strip()) for line in file]))
        file.close()
    return np.array(Z)


def read_z(path):
    file = open(path)
    Z = np.array([float(line.strip()) for line in file])
    file.close()
    return Z


def write(Z):
    write(Z, 'Gen', 'Zi_')
    for i in range(len(Z)):
        file = open('Gen/Zi_' + str(i + 1) + '.txt', 'w')
        for num in Z[i]:
            file.write(str(num) + '\n')
        file.close()


def write(data, path, name):
    for i in range(len(data)):
        file = open(path + '/' + name + str(i + 1) + '.txt', 'w')
        for num in data[i]:
            file.write(str(num) + '\n')
        file.close()

