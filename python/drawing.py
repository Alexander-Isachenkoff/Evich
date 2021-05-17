import matplotlib.pyplot as plt


def draw_graph(l, name, is_grid, graf_count):
    fig, axes = plt.subplots(graf_count, 1, figsize=(16, 8))

    for i, ax in enumerate(fig.axes):
        ax.plot(l[i])
        ax.grid(is_grid)
        ax.set_ylabel(name + "_%d" % (i + 1))

    plt.show()

