## this file generates random DNA strands data and writes it to a file
## currently we don't check if we are generating a new point or not

import sys
import random

DNAcodes = ['a', 'g', 'c', 't']

def getRandomDNACode():
    i = random.randint(0, len(DNAcodes) - 1)
    return DNAcodes[i]

def generateData():
    if (len(sys.argv) != 4):
        print "Usage: python dna_generator.py <numPoints> <ptDimension> <outputFile>"
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
            point.append(getRandomDNACode())
        outputFile.write(','.join(point) + "\n")
    return

if __name__ == "__main__":
    generateData()
