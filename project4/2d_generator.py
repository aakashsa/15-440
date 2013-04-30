## this file generates random DNA strands data and writes it to a file
## currently we don't check if we are generating a new point or not

import sys
import random

size = 1000.0

def getRandomFloatRange():
    num = random.randint(0, size)
    decimal = float(num)/size
    sign = num % 2
    if sign !=0:
        return num + decimal
    answer = -(num+decimal)    
    #print answer
    return answer


def generateData():
    if (len(sys.argv) != 5):
        print "Usage: python 2d_generator.py <numPoints> <ptDimension> <outputFile> <range>"
        sys.exit(0)

    size = sys.argv[4]    
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
            point.append(getRandomFloatRange())
        outputFile.write(str(tuple(point)) + "\n")
    return

generateData()
