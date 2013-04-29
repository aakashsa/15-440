import random
import sys
import copy
import utils
from pointdna import PointDNA

'''
	Compute the new centroid for a given list of points.
	For each dimension of the centroid, pick the DNA component
	that is most frequent in the given list of points
'''
def find_new_centroid(points):
	if (len(points)==0):
		return
	dimension = len(points[0])
	answer = []
	for i in range(0, dimension):
		# Most Frequent
		chars = {'a': 0, 'c': 0, 'g': 0, 't': 0}
		for point in points:
			chars[point[i]] = chars[point[i]] + 1
		max_char = 'a'
		max_count = chars[max_char]
		for ch in chars:
			if (chars[ch] >= max_count):
				max_char = ch
				max_count = chars[ch]
		answer.append(max_char)
	return PointDNA(answer)
	
'''
	Actual K Means Main Code 
'''
def k_means (points, k, centroids):
	iteration = 0
	while (True):
		new_centroid_list = []
		cluster_lists = utils.assign_cluster(points, centroids, k)
		for list_item in cluster_lists:
			new_centroid_list.append(find_new_centroid(list_item))
		#print "\n Old Centroids = ", PointDNA.stringify(centroids)
		#print "\n New Centroids = ", PointDNA.stringify(new_centroid_list)
		if (set(centroids) == set(new_centroid_list)):
			iteration += 1
			print "Took " + str(iteration) + " iteration(s)"
			return centroids
		centroids = copy.deepcopy(new_centroid_list)
		iteration += 1
	print "Took " + str(iteration) + " iteration(s)"
	return centroids

if __name__ == "__main__":
	if (len(sys.argv) != 4):
		print "Usage: python sequential_dna_kmeans.py <pointsFile> <k> <centroidsFile>"
		sys.exit(0)
	k = int(sys.argv[2])
	if (k <= 0):
		print "k must be at least 1"
		sys.exit(0)

	# Read data points
	points = utils.read_DNA_points(sys.argv[1])

	if (k > len(points)):
		print "k must be at most the number of data points"
		sys.exit(0)

	# Read centroids from file
	centroids = utils.read_DNA_points(sys.argv[3])

	if (len(centroids) != k):
		print "k must be equal to number of centroids"
		sys.exit(0)

	# Start k means algorithm
	result = k_means(points, k, centroids)
	print "Final centroids = " + PointDNA.stringify(result)
