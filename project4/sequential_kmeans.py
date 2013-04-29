import random
import sys
import copy
import utils
from point2d import Point2D
import time

'''
	Actual K Means Main Code 
'''	
def k_means (points, k, centroids):
	iteration = 0
	while (True):
		new_centroids = []
		# Assign data points to clusters
		cluster_lists = utils.assign_cluster(points, centroids, k)

		# compute new centroids as average of points in each cluster
		for list_item in cluster_lists:
			new_centroids.append(Point2D.getAverage(list_item))
		#print "\n Old Centroids ", Point2D.stringify(centroids)
		#print "\n New Centroids ", Point2D.stringify(new_centroids)

		# Check for convergence
		if (set(centroids) == set(new_centroids)):
			iteration += 1
			print "Took " + str(iteration) + " iteration(s)"
			return centroids

		# Iterate again if not convered
		centroids = copy.deepcopy(new_centroids)
		iteration += 1
	print "Took " + str(iteration) + " iteration(s)"
	return centroids

if __name__ == "__main__":
	if (len(sys.argv) != 4):
		print "Usage: python sequential_kmeans.py <pointsFile> <k> <centroidsFile>"
		sys.exit(0)
	k = int(sys.argv[2])
	if (k <= 0):
		print "k must be at least 1"
		sys.exit(0)

	# Get data points
	points = utils.read_2D_points(sys.argv[1])

	if (k > len(points)):
		print "k must be at most the number of data points"
		sys.exit(0)
	
	# Get centroids
	centroids = utils.read_2D_points(sys.argv[3])

	if (len(centroids) != k):
		print "k must be equal to number of centroids"
		sys.exit(0)

	# Run sequential K means
	start_time = time.time()
	result = k_means(points, k, centroids)
	end_time = time.time()
	print "Final centroids = " + Point2D.stringify(result)
	print "Sequential Kmeans on 2D data set took " + str(end_time - start_time) + " second(s)"