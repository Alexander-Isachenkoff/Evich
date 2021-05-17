import matplotlib.pyplot as plt
import math
from python import steps_filtration as stf, noise_filtration as nf, drawing, z_io
import numpy as np


def ssd(data1, data2):
    return np.sum(np.square(np.subtract(data1, data2)))


if __name__ == '__main__':
    fi = 0.8
    sigma = 0.1
    per = 5
    count = 99
    scale = 10000
    n = 4

    Ar = [0.49919369, 0.9404525, 0.4547725086, 0.841137265]
    SRm = [math.sqrt(0.275164), math.sqrt(0.32006153), math.sqrt(0.561166371), math.sqrt(0.25584871)]

    RegC = [[-0.003740645, 0, -0.249283006, -0.0212369],
            [0.044859474, 0, -0.098291637, 0.061969925],
            [0, 0, 0, 0]]

    M_S = [[290, 49, 159, 27],
           [0, 100, 0, 0]]

    A_S = [[0, -34.42789, -17.68, -29.2521991],
           [15, -19.42789, -0.68, -10.2521991],
           [0, -1.42789, 0, 0]]

    Ks = 3

    # y0 = mg.generate_metrics(Ar, SRm, count, n)
    # fixed.drawing.draw_graph(y0, "Исходный ряд", False, y0.shape[0])
    # fixed.z_io.write(y0)

    # y0 = fixed.z_io.read(n)
    # # # fixed.drawing.draw_graph(y0, "Исходный ряд", False, y0.shape[0])
    # #
    # trends = tr.generate_trends(count, n, RegC)
    # # fixed.drawing.draw_graph(trends, "Тренд", False, y0.shape[0])
    # #
    # y = y0 + trends
    # #
    # steps = stg.generate_steps(n, count, M_S, A_S)
    # # # fixed.drawing.draw_graph(steps, "Скачок", False, y0.shape[0])
    # #
    # y = y + steps
    # # fixed.drawing.draw_graph(y, "Y + скачок", False, y.shape[0])

    # fixed.z_io.write(y, 'y', 'y_')

    # fig, axes = plt.subplots(n, 1, figsize=(16, 8))
    # for i, ax in enumerate(fig.axes):
    #     ax.plot(y[i], '-b', markevery=range(0, count, 25))
    #     ax.grid(False)
    # plt.show()

    z = []
    z.append(np.zeros(99))
    z.append(z_io.read_z('download/Z1_19.txt'))
    z.append(z_io.read_z('download/Z2_19.txt'))
    z.append(z_io.read_z('download/Z3_19.txt'))
    n = len(z)
    z = np.array(z) * 10e6
    # z = y[0] - y
    drawing.draw_graph(z, "z", False, n)

    # z = y0[0] - y0
    # fixed.drawing.draw_graph(z, "Разностные ряды", False, y0.shape[0])

    #count = z.shape[1]
    #n = z.shape[0]
    # noise = ng.generate_noise(count, n, per, SRm[2] * scale)
    # #
    # z += noise
    # fixed.z_io.write(z, 'z', 'z_')

    # z = []
    # for i in range(n):
    #     file = open('z/z_' + str(i + 1) + '.txt')
    #     z.append(np.array([float(line.strip()) for line in file]))
    #     file.close()
    #
    # z = np.array(z)

    # fixed.drawing.draw_graph(z, "Z + Выбросы", False, y0.shape[0])
    fig, axes = plt.subplots(n, 1, figsize=(16, 9))
    for i, ax in enumerate(fig.axes):
        ax.plot(z[i], 'b')
        ax.set_ylabel('z' + "_%d" % (i + 1))
        ax.set_title('Генератор ' + str(i + 1))
        ax.set_xlabel('Такты')
        ax.grid(False)
    plt.subplots_adjust(hspace=0.65, top=0.95)
    plt.legend(['ряды измерений, содержащие нестационарности'], bbox_to_anchor=(0, -0.5, 1, 0), loc=9, ncol=2, borderaxespad=0)
    # fig.tight_layout()
    plt.show()

    # ssd_res = np.zeros((n, 2))
    # for i in range(n):
    #     ssd_res[i][0] = ssd(y0[i], z[i]+y0[0])

    z_filtered_noise = nf.filter_noise(z, count)

    fig, axes = plt.subplots(n, 1, figsize=(16, 9))
    for i, ax in enumerate(fig.axes):
        ax.plot(z_filtered_noise[i], 'b')
        ax.set_ylabel('z_filtered_noise' + "_%d" % (i + 1))
        ax.set_title('Генератор ' + str(i + 1))
        ax.set_xlabel('Такты')
        ax.grid(False)
    plt.subplots_adjust(hspace=0.65, top=0.95)
    plt.legend(['Отфильтрованы выбросы'], bbox_to_anchor=(0, -0.5, 1, 0), loc=9, ncol=2, borderaxespad=0)
    plt.show()

    # fig, axes = plt.subplots(n, 1, figsize=(16, 8))
    # for i, ax in enumerate(fig.axes):
    #     ax.plot(z[i] - noise[i], 'b', z_filtered_noise[i], 'r')
    #     ax.grid(False)
    # plt.show()

    # fig, axes = plt.subplots(n, 1, figsize=(16, 8))
    # for i, ax in enumerate(fig.axes):
    #     ax.plot(z_filtered_noise[i], 'b', markevery=range(0, count, 25))
    #     ax.set_ylabel('z' + "_%d" % (i + 1))
    #     ax.set_title('Генератор ' + str(i + 1))
    #     ax.set_xlabel('Такты')
    #     ax.grid(False)
    # plt.subplots_adjust(hspace=0.65, top=0.95)
    # plt.legend(['ряд со ступенчатыми функциями и трендами'], bbox_to_anchor=(0, -0.5, 1, 0),
    #            loc=9, ncol=2, borderaxespad=0)
    # plt.show()

    # fig, axes = plt.subplots(n, 1, figsize=(16, 8))
    # for i, ax in enumerate(fig.axes):
    #     ax.plot(y0[i], 'b')
    #     ax.plot(z_filtered_noise[i], '--rs', markevery=range(0, count, 25))
    #     ax.set_ylabel('z' + "_%d" % (i + 1))
    #     ax.set_title('Генератор ' + str(i + 1))
    #     ax.set_xlabel('Такты')
    #     ax.grid(False)
    # plt.subplots_adjust(hspace=0.65, top=0.95)
    # plt.legend(['"истинный" ряд', 'ряд со ступенчатыми функциями и трендами'], bbox_to_anchor=(0, -0.5, 1, 0),
    #            loc=9, ncol=2, borderaxespad=0)
    # plt.show()

    # expected_steps, found_steps, Z_no_lin, YnLin = stf.filter_steps(z_filtered_noise, Ks, A_S, M_S)
    found_steps, Z_no_lin, YnLin = stf.filter_steps2(z_filtered_noise, Ks)

    # fixed.drawing.draw_graph(found_steps, "Найденные скачки", False, found_steps.shape[0])
    # fig, axes = plt.subplots(n, 1, figsize=(16, 8))
    # for i, ax in enumerate(fig.axes):
    #     ax.plot(steps[i], 'b')
    #     ax.plot(found_steps[i], '--rs', markevery=range(0, count, 25))
    #     ax.set_ylabel('St' + "_%d" % (i + 1))
    #     ax.set_title('Генератор ' + str(i + 1))
    #     ax.set_xlabel('Такты')
    #     ax.grid(False)
    # plt.plot(0, 0, '-b')
    # plt.plot(0, 0, '--rs')
    # plt.subplots_adjust(hspace=0.65, top=0.95)
    # plt.legend(['"истинные" ступенчатые функции', 'найденные ступенчатые функции'], bbox_to_anchor=(0, -0.5, 1, 0),
    #            loc=9, ncol=2, borderaxespad=0)
    # plt.show()

    fig, axes = plt.subplots(n, 1, figsize=(16, 9))
    for i, ax in enumerate(fig.axes):
        ax.plot(found_steps[i], 'b')
        ax.set_ylabel('St' + "_%d" % (i + 1))
        ax.set_title('Генератор ' + str(i + 1))
        ax.set_xlabel('Такты')
        ax.grid(False)
    plt.subplots_adjust(hspace=0.65, top=0.95)
    plt.legend(['Найденные ступенчатые функции'], bbox_to_anchor=(0, -0.5, 1, 0), loc=9, ncol=2, borderaxespad=0)
    plt.show()

    fig, axes = plt.subplots(n, 1, figsize=(16, 9))
    for i, ax in enumerate(fig.axes):
        ax.plot(Z_no_lin[i], 'b')
        ax.set_ylabel('Z_no_lin' + "_%d" % (i + 1))
        ax.set_title('Генератор ' + str(i + 1))
        ax.set_xlabel('Такты')
        ax.grid(False)
    plt.subplots_adjust(hspace=0.65, top=0.95)
    plt.legend(['Отфильтрованы скачки'], bbox_to_anchor=(0, -0.5, 1, 0), loc=9, ncol=2, borderaxespad=0)
    plt.show()

    # fixed.drawing.draw_graph(Z_no_lin, "Z без скачков", False, steps.shape[0])
    #
    # fig, axes = plt.subplots(n, 1, figsize=(16, 8))
    # for i, ax in enumerate(fig.axes):
    #     ax.plot(y0[i], 'b')
    #     ax.plot(YnLin[i], '-rs', markevery=[i for i in range(0, count, 25)])
    #     ax.set_ylabel('Y' + "_%d" % (i + 1))
    #     ax.grid(False)
    # plt.show()

    fig, axes = plt.subplots(n, 1, figsize=(16, 9))
    for i, ax in enumerate(fig.axes):
        ax.plot(YnLin[i], 'b')
        ax.set_ylabel('YnLin' + "_%d" % (i + 1))
        ax.set_title('Генератор ' + str(i + 1))
        ax.set_xlabel('Такты')
        ax.grid(False)
    plt.subplots_adjust(hspace=0.65, top=0.95)
    plt.legend([''], bbox_to_anchor=(0, -0.5, 1, 0), loc=9, ncol=2, borderaxespad=0)
    plt.show()

    y1lin, y2lin, y_a_b = [], [], []
    for j in range(n):
        y1lin.append(stf.reg_lin(YnLin[j], count))
        y2lin.append(stf.reg_sq(YnLin[j]))
        y_a_b.append(stf.reg_a_b(YnLin[j]))

    # fixed.drawing.draw_graph(y1lin, "y1lin", False, steps.shape[0])

    y_a_b[0] = [-0.326, 0.048]
    # fixed.drawing.draw_graph(y_a_b, "y_a_b", False, steps.shape[0])

    # fig, axes = plt.subplots(n, 1, figsize=(16, 8))
    # for i, ax in enumerate(fig.axes):
    #     ax.plot(trends[i], 'b', y1lin[i], 'r')
    #     ax.grid(False)
    # plt.show()

    fig, axes = plt.subplots(n, 1, figsize=(16, 9))
    for i, ax in enumerate(fig.axes):
        ax.plot(y1lin[i], 'b')
        ax.set_ylabel('Tr' + "_%d" % (i + 1))
        ax.set_title('Генератор ' + str(i + 1))
        ax.set_xlabel('Такты')
        ax.grid(False)
    plt.subplots_adjust(hspace=0.65, top=0.95)
    plt.legend(['Найденные тренды'], bbox_to_anchor=(0, -0.5, 1, 0), loc=9, ncol=2, borderaxespad=0)
    plt.show()

    # fig, axes = plt.subplots(n, 1, figsize=(16, 9))
    # for i, ax in enumerate(fig.axes):
    #     ax.plot(trends[i], 'b')
    #     ax.plot(y1lin[i], 'r', markevery=range(0, count, 10))
    #     ax.set_ylabel('tr' + "_%d" % (i + 1))
    #     ax.set_title('Генератор ' + str(i + 1))
    #     ax.set_xlabel('Такты')
    #     ax.grid(False)
    # plt.subplots_adjust(hspace=0.65, top=0.95)
    # plt.legend([' "истинные" тренды', 'результат работы алгоритма'], bbox_to_anchor=(0, -0.5, 1, 0), loc=9, ncol=2, borderaxespad=0)
    # # fig.tight_layout()
    # plt.show()


    # Lin_Y = []
    Yres_lin = np.zeros((n, count))
    for j in range(n):
        # Lin_Y.append(stf.Fisher2(YnLin[j], y1lin[j], y2lin[j], count))
        Yres_lin[j] = stf.res_Fish(YnLin[j], y1lin[j], y2lin[j], count)

    # fixed.drawing.draw_graph(Yres_lin, "Yres_lin", False, 4)

    # fig, axes = plt.subplots(n, 1, figsize=(16, 9))
    # for i, ax in enumerate(fig.axes):
    #     ax.plot(y0[i], 'b')
    #     ax.plot(Yres_lin[i], '--rs', markevery=range(0, count, 10))
    #     ax.set_ylabel('Yresult' + "_%d" % (i + 1))
    #     ax.set_title('Генератор ' + str(i + 1))
    #     ax.set_xlabel('Такты')
    #     ax.grid(False)
    # plt.subplots_adjust(hspace=0.65, top=0.95)
    # plt.legend([' "истинный" ряд', 'результат работы алгоритма'], bbox_to_anchor=(0, -0.5, 1, 0), loc=9, ncol=2, borderaxespad=0)
    # # fig.tight_layout()
    # plt.show()

    fig, axes = plt.subplots(n, 1, figsize=(16, 9))
    for i, ax in enumerate(fig.axes):
        ax.plot(Yres_lin[i], 'b')
        ax.set_ylabel('Z' + "_%d" % (i + 1))
        ax.set_title('Генератор ' + str(i + 1))
        ax.set_xlabel('Такты')
        ax.grid(False)
    plt.subplots_adjust(hspace=0.65, top=0.95)
    plt.legend(['Стационарные составляющие'], bbox_to_anchor=(0, -0.5, 1, 0), loc=9, ncol=2, borderaxespad=0)
    plt.show()

    # YnLin = -z_filtered_noise - y0[0]
    # YnLin = -z - y0[0]

    YnLin = Yres_lin

    # for i in range(n):
    #     ssd_res[i][1] = ssd(y0[i], YnLin[i])
    # print("МНК:\n", ssd_res)
    #
    # mnk_sum = np.sum(ssd_res.transpose()[1])
    # print("МНК sum:", mnk_sum)

    expected_values = np.zeros((n, 2))
    for i in range(n):
        # expected_values[i][0] = np.mean(y0[i])
        expected_values[i][1] = np.mean(YnLin[i])
    print("expected values:\n", expected_values)

    variations = np.zeros((n, 2))
    for i in range(n):
        # variations[i][0] = np.var(y0[i])
        variations[i][1] = np.var(YnLin[i])
    print("variations:\n", variations)

    # absolute_error = (YnLin - y0)
    # print("absolute error:\n")
    # for i in range(np.shape(M_S)[0]):
    #     for j in range(np.shape(M_S)[1]):
    #         index = M_S[i][j]
    #         if index != 0:
    #             print(j, index, absolute_error[j][index])

    # print("\nМат. ожидания погрешностей:")
    # for i in range(len(absolute_error)):
    #     print(i+1, '-', np.mean(absolute_error[i]))
    #
    # print("\nДисперсии погрешностей:")
    # for i in range(len(absolute_error)):
    #     print(i+1, '-', np.var(absolute_error[i]))
    #
    # print("\nПроверка гипотезы:")
    # for i in range(len(absolute_error)):
    #     t = np.abs(np.mean(absolute_error[i])) / np.std(absolute_error[i]) * np.sqrt(len(absolute_error[i]))
    #     print(i+1, '-', t, t < 1.96)
    #
    # relative_error = absolute_error / (y0 + steps) * 100
    # print("\nrelative error:")
    # for i in range(np.shape(M_S)[0]):
    #     for j in range(np.shape(M_S)[1]):
    #         index = M_S[i][j]
    #         if index != 0:
    #             print(j, index, relative_error[j][index], "%")

    z_io.write(YnLin, 'cleaned', 'YnLin_')
