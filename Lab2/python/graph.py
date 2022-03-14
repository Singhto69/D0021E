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
            sx = numpy.append(sx, float(numpy.asarray([line[0]]))).astype(float)
            sy = numpy.append(sy, float(numpy.asarray([line[1]]))).astype(float)

        else:
            line = line[6:]
            line = line.split(":")
            rx = numpy.append(rx, float(numpy.asarray([line[0]]))).astype(float)
            ry = numpy.append(ry, float(numpy.asarray([line[1]]))).astype(float)

    return sx, sy, rx, ry


x = numpy.array([])
x2 = numpy.append(x, numpy.array([1]))

#cbrsx, cbrsy, cbrrx, cbrry = parser(open("../logcbr.txt", "r"))
gausx, gausy, gaurx, gaury = parser(open("../loggaussian.txt", "r"))
poisx, poisy, poirx, poiry = parser(open("../logpoisson.txt", "r"))


def plot(sx, sy, rx, ry):
    plt.title("Line graph")
    plt.xlabel("X axis")
    plt.ylabel("Y axis")
    plt.scatter(sx, sy, color="green")
    plt.scatter(rx, ry, color="red")
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
