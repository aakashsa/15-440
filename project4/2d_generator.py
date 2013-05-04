import utils
import random
import numpy
import sys
from point2d import Point2D

def generate_clusters(k, clusterOutput, maxValue):
	'''
	Generate random k cluster centroids within radius maxValue and write to
	clusterOutput file
	'''
	output = open(clusterOutput, 'w')
	centroids = []
	for i in xrange(k):
		l = list(numpy.random.uniform(0, maxValue, 2))
		centroid = Point2D(l[0], l[1])
		# is it far enough from the others?
		while (centroid in centroids):
			l = list(numpy.random.uniform(0, maxValue, 2))
			centroid = Point2D(l[0], l[1])
		centroids.append(centroid)
		output.write(str(centroid.x) + "," + str(centroid.y) + "\n")
	return centroids

def generate_points(clusters, maxValue, ptsPerCluster, outputFile):
	'''
	Given a list of centroids, generate ptsPerCluster many points for each
	cluster within radius of maxValue and write to outputFile
	'''
	output = open(outputFile, 'w')
	minClusterVar = 0
	maxClusterVar = 0.5
	for cluster in clusters:
		variance = numpy.random.uniform(minClusterVar, maxClusterVar)
		for i in xrange(ptsPerCluster):
			(x,y) = numpy.random.normal((cluster.x, cluster.y), variance)
			output.write(str(x) + "," + str(y) + "\n")

if __name__ == "__main__":
	if (len(sys.argv) != 6):
		print "Usage: python 2d_generator_2.py <numPointsPerCluster> " + \
			"<maxValue> <k> <clusterOutput> <pointsOutput>"
		sys.exit(0)

	ptsPerCluster = int(sys.argv[1])
	if (ptsPerCluster <= 0):
		print "ERROR: Points per cluster must be at least 1"
		sys.exit(0)
	maxValue = int(sys.argv[2])    
	if (maxValue < 0):
		print "ERROR: Max value must be at least 0"
		sys.exit(0)
	k = int(sys.argv[3])
	if (k <= 0):
		print "ERROR: k must be at least 1"
		sys.exit(0)

	clusters = generate_clusters(k, sys.argv[4], maxValue)
	generate_points(clusters, maxValue, ptsPerCluster, sys.argv[5])