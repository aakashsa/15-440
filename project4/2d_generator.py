## this file generates random 2D Points data and writes it to a file
## currently we don't check if we are generating a new point or not

import sys
import random

def getRandomFloatRange(size):
    num = random.randint(0, size-1)
    decimal = random.random()
    sign = num % 2
    if sign !=0:
        return num + decimal
    answer = -(num+decimal)    
    return answer


def generateData():
    if (len(sys.argv) != 5):
        print "Usage: python 2d_generator.py <numPoints> <ptDimension> <outputFile> <range>"
        sys.exit(0)
    size = int(sys.argv[4])    
    if (size <= 0):
        print "ERROR: Range must be at least 1"
        sys.exit(0)

    numPoints = int(sys.argv[1])
    if (numPoints <= 0):
        print "ERROR: Num points must be at least 1"
        sys.exit(0)
    ptDimension = int(sys.argv[2])
    if (ptDimension <= 0):
        print "ERROR: Point dimension must be at least 1"
        sys.exit(0)

    outputFile = open(sys.argv[3], 'w')

    # generate points
    for pt in xrange(numPoints):
        point = []
        for i in xrange(ptDimension):
            point.append(getRandomFloatRange(size))
        outputFile.write(str(point[0]) + "," + str(point[1]) + "\n")
    return

if __name__ == "__main__":
    generateData()
