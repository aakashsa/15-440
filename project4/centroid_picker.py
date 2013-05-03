import random
import sys

def pickCentroids(k, pointsFile, outputFile):
	'''
	Functionality to pick centroids from an input file
	Picks k random centroids from point file, and writes them to given output 
	file
	'''
	output = open(outputFile, 'w')
	inputPoints = [line for line in open(pointsFile)]
	centroids = random.sample(inputPoints, k)
	for centroid in centroids:
		output.write(centroid)

# Centroid picker runner
if __name__ == "__main__":
	if (len(sys.argv) != 4):
		print "Usage: python centroid_picker.py <pointsFile> <k> <outputFile>"
		sys.exit(0)
	k = int(sys.argv[2])
	if (k <= 0):
		print "k must be at least 1"
		sys.exit(0)
	pickCentroids(k, sys.argv[1], sys.argv[3])

