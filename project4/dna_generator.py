import utils
import random
import numpy
import sys
from pointdna import PointDNA

def tooClose(point, points, maxSimilarity):
	'''
	Computes the DNA similarity between the point and all points
	in the list, and if any points in the list are more similar than maxSimilarity,
	this method returns true.
	'''
	for pt in points:
		if point.distance(pt) > maxSimilarity:
			return True

	return False

def get_component(seed):
	'''
	Given a random seed between 0 (inclusive) and 4 (exclusive), return
	the relevant component
	'''
	if (seed < 1):
		return 'a'
	if (seed < 2):
		return 'c'
	if (seed < 3):
		return 'g'
	return 't'

def generate_clusters(k, clusterOutput, dimension):
	'''
	Generate random k cluster DNA centroids each of length dimension and write
	to clusterOutput file. It returns the actual float value corresponding
	to each dimension of each of the generated clusters
	'''
	output = open(clusterOutput, 'w')
	centroids_seeds = []
	centroids = []
	maxSimilarity = dimension/2
	for i in xrange(k):
		l = list(numpy.random.uniform(0, 4, dimension))
		component_l = [get_component(seed) for seed in l]
		centroid = PointDNA(component_l)
		# is it far enough from the others?
		while (tooClose(centroid, centroids, maxSimilarity)):
			l = list(numpy.random.uniform(0, 4, dimension))
			component_l = [get_component(seed) for seed in l]
			centroid = PointDNA(component_l)
		centroids.append(centroid)
		centroids_seeds.append(l)
		output.write(','.join(component_l) + "\n")
	return centroids_seeds

def generate_points(clusters_seeds, dimension, ptsPerCluster, outputFile):
	'''
	Given a list of centroid seeds, generate ptsPerCluster many DNA points, each of
	length dimension for each cluster and write to outputFile
	'''
	output = open(outputFile, 'w')
	minClusterVar = 0
	maxClusterVar = 0.5
	for cluster_seed in clusters_seeds:
		variance = numpy.random.uniform(minClusterVar, maxClusterVar)
		for i in xrange(ptsPerCluster):
			l = list(numpy.random.normal(cluster_seed, variance))
			component_l = [get_component(seed) for seed in l]
			output.write(','.join(component_l) + "\n")

if __name__ == "__main__":
	if (len(sys.argv) != 6):
		print "Usage: python dna_generator.py <numPointsPerCluster> " + \
			"<dimension> <k> <clusterOutput> <pointsOutput>"
		sys.exit(0)

	ptsPerCluster = int(sys.argv[1])
	if (ptsPerCluster <= 0):
		print "ERROR: Points per cluster must be at least 1"
		sys.exit(0)
	dimension = int(sys.argv[2])
	if (dimension <= 0):
		print "ERROR: Dimension must be at least 1"
		sys.exit(0)
	k = int(sys.argv[3])
	if (k <= 0):
		print "ERROR: k must be at least 1"
		sys.exit(0)

	clusters_seeds = generate_clusters(k, sys.argv[4], dimension)
	generate_points(clusters_seeds, dimension, ptsPerCluster, sys.argv[5])