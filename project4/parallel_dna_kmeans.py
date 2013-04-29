from mpi4py import *
import random
import sys
import copy
import operator
from pointdna import PointDNA

'''
	Given a List of Points it picks a bunch of k unique random centroids
'''
def init_centroids(points,k):
	i = 0
	includedPoints = set()
	centroids = []
	while (i < k):
		newPointIndex = random.randrange(0,len(points))
		if newPointIndex not in includedPoints:
			includedPoints.add(newPointIndex)
			centroids.append(points[newPointIndex])
			i += 1
	return centroids

'''
	Find the closest centroid index to a given point from a given list of centroids
	return: index of centroid to which this point is closest to
'''
def find_closest_centroid(point, centroids):
	min_dist = sys.maxint
	cur_centroid = 0
	for i in xrange(0, len(centroids)):
		icentroid_dist = point.distance(centroids[i])
		if (icentroid_dist <= min_dist):
			min_dist = icentroid_dist
			cur_centroid = i
	return cur_centroid

'''
	Given a set of points partition them into a set of n subset lists
'''
def partition_points(points, n):
	answer =  [ [] for i in range(n) ]
	for i in range(0,len(points)):
		answer[(i%n)].append(points[i])
	return answer

'''
	Given a List of Points it returns k lists where each list is the 
	bunch of points corresponding to a 
'''	
def assign_cluster(points, centroids, k):
	assignments =  [ [] for i in range(k) ]
	for point in points:
		new_assignment = find_closest_centroid(point, centroids)
		assignments[new_assignment].append(point)	
	return assignments

'''
	dimension_stats_all_nodes = 3D list of dimension stats; for each node, for each cluster, for each dimension
	k = number of clusters
	dimension = dimension of a DNA strand
'''
def recalculate_centroids(dimension_stats_all_nodes, k, dimension):
	# initialize the new centroids
	new_centroids = [[] for i in xrange(k)]

	# for each cluster make a list of all the dictionary lists
	for i in xrange(k):
		# 2D list of dicts; n x dimension
		cluster_dicts = [node_stats[i] for node_stats in dimension_stats_all_nodes]
		# compute new centroid for cluster i
		for j in xrange(dimension):
			dictionary = {'a': 0, 'c':0, 'g':0, 't':0} 
			for cluster_dict in cluster_dicts:
				d = cluster_dict[j] # dictionary for j-th dimension in i-th cluster
				keys = d.keys()
				# update the values for all the keys
				for k in keys:
					dictionary[k] += d[k]
			# append the most frequent letter for that base to the
			# new centroid
			max_char = 'a'
			max_count = dictionary[max_char]
			for ch in dictionary.keys():
				if (dictionary[ch] >= max_count):
					max_char = ch
					max_count = dictionary[ch]
			new_centroids[i].append(max_char)
		new_centroids[i] = PointDNA(new_centroids[i])
	return new_centroids

'''
	This function gets the total counts of each of the four characters in each dimension
	return: a list of length dimension of dictionaries; dict i contains counts for each character at dimension i
'''
def getDimensionWiseStats(clusterPoints, dimension):
	dicts = [{'a': 0, 'c':0, 'g':0, 't':0} for i in xrange(dimension)]

	# if there are no points in this cluster, all counts are 0
	if (len(clusterPoints) == 0):
		return dicts

	for i in xrange(dimension):
		for point in clusterPoints:
			dicts[i][point[i]] += 1
	return dicts

'''
	Master Functionality Main K_means Code
	points = list of tuples
	k = number of clusters
'''
def master_function(points, k):
	# Initial Centroids
	centroids = init_centroids(points,k)
	#centroids = [Point2D(3,3), Point2D(-3,3), Point2D(0.5,0.5), Point2D(-3,-3), Point2D(3,-3), Point2D(0,0)]
	if (len(centroids) != k):
		print "k must be equal to number of centroids"
		sys.exit(0)
	comm = MPI.COMM_WORLD

	num_nodes = comm.Get_size()

	# Partition The points for the slaves
	partition = partition_points(points, num_nodes - 1)

	# 3D list of dictionaries: for each node, for each cluster, for each dimension (n x k x dimension)
	dimension_stats_all_nodes =  [ [] for i in xrange(num_nodes - 1) ]
	iteration = 0
	dimension = len(points[0])
	
	# Send k and the Data Points to the Slaves
	for i in xrange (1, num_nodes):
		comm.send(k, dest = i)
		comm.send(partition[i-1], dest = i)

	while (True):
		# Send the Initial Centroids
		comm.bcast(centroids)

		# Receive centroids and population count for EACH centroid from slaves
		for i in xrange (1, num_nodes):
	   		dimension_stats_all_nodes[i-1] = comm.recv(source = i)

		# Recalculating the New Centroids Based on the Global FeedBack
		new_centroids = recalculate_centroids(dimension_stats_all_nodes, k, dimension)
		print " Old Centroids = ", PointDNA.stringify(centroids)
		print " New Centroids = ", PointDNA.stringify(new_centroids)

		# If Centroids Haven't Changed then We are done and we send empty list to slaves to signal end
		if (set(centroids) == set(new_centroids)):
	   		comm.bcast([], root = 0)
	   		iteration += 1
	   		print "Took " + str(iteration) + " iteration(s)"
			return centroids
		# New Centroid Copy
		centroids = copy.deepcopy(new_centroids)
		new_centroids = []
		iteration += 1
	print "Took " + str(iteration) + " iteration(s)"
	return centroids

'''
	Slave Functionality Main K_means Code
'''
def slave_function():
	# Get communication instance
	comm = MPI.COMM_WORLD

	# Get k
	k = comm.recv(source = 0)

	# Get Data Points	
	data_points = comm.recv(source = 0)
	print " Received Points = ", PointDNA.stringify(data_points), " on node ", rank

	# Dimension
	dimension = len(data_points[0])

	# Dimension stats for all clusters
	dimension_stats_all_clusters = []

	centroids = []
	while (True):
		# Receive centroids from master
		centroids = comm.bcast(centroids,root=0) 
		print "Received Centroids ", PointDNA.stringify(centroids)
		
		# check if done signal is received
		if len(centroids) == 0:
			return 
		# Assign points to clusters (2D list of points)
		assigned_points = assign_cluster(data_points, centroids, k)
		for i in range (0, k):
			# dimension stats is a list of dictionaries
			dimension_stats_cluster_i = getDimensionWiseStats(assigned_points[i], dimension)
			dimension_stats_all_clusters.append(dimension_stats_cluster_i)
		# Send the dimension stats for all clusters to master node
	   	comm.send(dimension_stats_all_clusters, dest=0)


if __name__ == "__main__":
	comm = MPI.COMM_WORLD
	rank = comm.Get_rank()

	if rank == 0:
		if (len(sys.argv) != 3):
			print "Usage: python parallel_dna_kmeans.py <pointsFile> <k>"
			sys.exit(0)
		k = int(sys.argv[2])
		if (k <= 0):
			print "k must be at least 1"
			sys.exit(0)
		f = open(sys.argv[1])
		strDNAPoints = [line.strip() for line in open(sys.argv[1])]
		if (len(strDNAPoints) == 0):
			print "Points file is empty"
			sys.exit(0)
		points = []
		for pt in strDNAPoints:
			points.append(PointDNA(pt.split(',')))

		if (k > len(points)):
			print "k must be at most the number of data points"
			sys.exit(0)
		
		# Run K means in parallel and get result
		centroids = master_function(points,k)
		print "Final centroids = " + PointDNA.stringify(centroids)
	else:
		slave_function()	
