import numpy
import cv2
import matplotlib.pyplot as plt


def parser(file):
    sx = numpy.array([])
    sy = numpy.array([])

    rx = numpy.array([])
    ry = numpy.array([])

    for line in file:
        line = line[:-1]
        if line[0] == "s":
            line = line[6:]
            line = line.split(":")
            sx = numpy.append(sx, int(numpy.asarray([line[0]]))).astype(int)
            sy = numpy.append(sy, int(numpy.asarray([line[1]]))).astype(int)

        else:
            line = line[6:]
            line = line.split(":")
            rx = numpy.append(rx, int(numpy.asarray([line[0]]))).astype(int)
            ry = numpy.append(ry, int(numpy.asarray([line[1]]))).astype(int)

    return sx, sy, rx, ry


x = numpy.array([])
x2 = numpy.append(x, numpy.array([1]))

cbrsx, cbrsy, cbrrx, cbrry = parser(open("../logcbr.txt", "r"))
gausx, gausy, gaurx, gaury = parser(open("../loggaussian.txt", "r"))
poisx, poisy, poirx, poiry = parser(open("../logpoisson.txt", "r"))


def plot(sx, sy, rx, ry):
    plt.title("Line graph")
    plt.xlabel("X axis")
    plt.ylabel("Y axis")
    plt.plot(sx, sy, color="green")
    plt.plot(rx, ry, color="red")
    plt.show()


def swapper(sx, sy, rx, ry):
    x = sx
    y = sy
    while True:
        plot(x, y)
        if cv2.waitKey(1) & 0xFF == ord('s'):
            x = sx
            y = sy
        if cv2.waitKey(1) & 0xFF == ord('r'):
            x = rx
            y = ry
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break


#plot(cbrsx, cbrsy, cbrrx, cbrry)
#plot(gausx, gausy, gaurx, gaury)
plot(poisx, poisy, poirx, poiry)
