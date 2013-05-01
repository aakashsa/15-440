from mpi4py import *
import random
import sys
import copy
import operator
from pointdna import PointDNA
import utils
import time

'''
	This function calculates new centroids based on the dimension statistics 
	received from all the nodes.
	dimension_stats_all_nodes = 3D list of dimension stats; for each node, for 
	each cluster, for each dimension
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
				# dictionary for j-th dimension in i-th cluster
				d = cluster_dict[j]
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
	This function gets the total counts of each of the four characters in 
	each dimension
	culsterPoints = list of points belonging to a particular cluster
	dimension = dimension of DNA strand
	return: a list of length dimension of dictionaries; dict i contains 
	counts for each character at dimension i
'''
def get_dimension_wise_stats(clusterPoints, dimension):
	dicts = [{'a': 0, 'c':0, 'g':0, 't':0} for i in xrange(dimension)]

	# if there are no points in this cluster, all counts are 0
	if (len(clusterPoints) == 0):
		return dicts

	for i in xrange(dimension):
		for point in clusterPoints:
			dicts[i][point[i]] += 1
	return dicts

'''
	Master functionality for K_means code
	points = list of data points
	k = number of clusters
	centroids = list of initial centroids
	return: list of final centroids
'''
def master_function(points, k, centroids):
	comm = MPI.COMM_WORLD

	num_nodes = comm.Get_size()

	# Partition The points for the slaves
	partition = utils.partition_points(points, num_nodes - 1)

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

		# Receive statistics on each dimension of each dna for each centroid
		for i in xrange (1, num_nodes):
	   		dimension_stats_all_nodes[i-1] = comm.recv(source = i)

		# Recalculating the New Centroids Based on the Global FeedBack
		new_centroids = recalculate_centroids(dimension_stats_all_nodes, k, dimension)

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
	Slave functionality K_means code
'''
def slave_function():
	# Get communication instance
	comm = MPI.COMM_WORLD

	# Get k
	k = comm.recv(source = 0)

	# Get Data Points	
	data_points = comm.recv(source = 0)

	# Dimension
	dimension = len(data_points[0])

	# Dimension stats for all clusters
	dimension_stats_all_clusters = []

	centroids = []
	while (True):
		# Receive centroids from master
		centroids = comm.bcast(centroids,root=0) 
		
		# check if done signal is received
		if len(centroids) == 0:
			return 
		# Assign points to clusters (2D list of points)
		assigned_points = utils.assign_cluster(data_points, centroids, k)
		for i in range (0, k):
			# dimension stats is a list of dictionaries
			dimension_stats_cluster_i = get_dimension_wise_stats(assigned_points[i], dimension)
			dimension_stats_all_clusters.append(dimension_stats_cluster_i)
		# Send the dimension stats for all clusters to master node
	   	comm.send(dimension_stats_all_clusters, dest=0)


if __name__ == "__main__":
	comm = MPI.COMM_WORLD
	rank = comm.Get_rank()

	if rank == 0:
		if (len(sys.argv) != 4):
			print "Usage: python parallel_dna_kmeans.py <pointsFile> <k> <centroidsFile>"
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
		
		# Get initial centroids
		centroids = utils.read_DNA_points(sys.argv[3])

		if (len(centroids) != k):
			print "k must be equal to number of centroids"
			sys.exit(0)

		# Run K means in parallel and get result
		start_time = time.time()
		result = master_function(points, k, centroids)
		end_time = time.time()
		print "Final centroids = " + PointDNA.stringify(result)
		print "Parallel Kmeans on DNA data set took " + str(end_time - start_time) + " second(s)"
	else:
		start_time = time.time()
		slave_function()
		end_time = time.time()
		print "Node " + str(rank) + " took " + str(end_time - start_time) + " second(s)"
