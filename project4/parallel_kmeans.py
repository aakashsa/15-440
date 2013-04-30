from mpi4py import *
import sys
import copy
import operator
from point2d import Point2D
import utils
import time

'''
	Centroids from all n-1 processess - centroid_local = 2D list. List i is 
	   centroids from node i
	population from all n-1 processess - population_local = 2D list. List i 
	   contains population for centroids from node i
	k = number of clusters
	return: new averaged centroids
'''
def recalculate_centroids(centroid_local, population_local, k):
	# Total size of each cluster
	total_size = [ 0.0 for i in range(k)]
	for i in range(0, k):
		for node in population_local:
			total_size[i] += node[i]

	# Initialize the Answer with the Weighed Centroids we got from Slave one
	new_centroids = [ None for i in range(k) ]

	# For Each Slaves Centroid List we have to weigh it with the population size in that cluster	
	for i in range(0, k):
		for j in range(0, len(centroid_local)):
			if (total_size[i] != 0 and centroid_local[j][i] is not None): # if this cluster is non empty
				factored = centroid_local[j][i].scalarMultiply(population_local[j][i]/total_size[i])
				new_centroids[i] = factored + new_centroids[i] # add points
	return new_centroids		

'''
	Master functionality K_means code
	points = list of initial data points
	k = number of clusters
	centroids = initial list of centroids
	return: list of final centroids
'''
def master_function(points, k, centroids):
	comm = MPI.COMM_WORLD

	num_nodes = comm.Get_size()

	# Partition The points for the slaves
	partition = utils.partition_points(points, num_nodes - 1)

	centroid_slave =  [ [] for i in xrange(num_nodes - 1) ]
	population_slave =   [ [] for i in xrange(num_nodes - 1) ]
	iteration = 0
	
	# Send k and the Data Points to the Slaves
	for i in xrange (1, num_nodes):
		comm.send(k, dest = i)
		comm.send(partition[i-1], dest = i)

	while (True):
		# Send the Initial Centroids
		comm.bcast(centroids)

		# Receive centroids and population count for EACH centroid from slaves
		for i in xrange (1, num_nodes):
	   		(centroid_slave[i-1], population_slave[i-1]) = comm.recv(source = i)

		# Recalculating the New Centroids Based on the Global FeedBack
		new_centroids = recalculate_centroids(centroid_slave, population_slave, k)

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

	# New Centroid List
	new_centroids =  [ [] for i in range(k) ]
	# Population List have size of each cluster
	population_slave = [ [] for i in range(k) ]
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
			# new_centroids: list of points
			new_centroids[i] = Point2D.getAverage(assigned_points[i])
			# population_slave: list of numbers; i-th number is no. of points in cluster i
			population_slave[i] =  len(assigned_points[i])
		# Send the new centroids and the population count of each cluster	
	   	comm.send((new_centroids, population_slave), dest=0)


if __name__ == "__main__":
	comm = MPI.COMM_WORLD
	rank = comm.Get_rank()

	if rank == 0:
		if (len(sys.argv) != 4):
			print "Usage: python parallel_kmeans.py <pointsFile> <k> <centroidsFile>"
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

		# Run K means in parallel and get result
		start_time = time.time()
		result = master_function(points, k, centroids)
		end_time = time.time()
		print "Final centroids = " + Point2D.stringify(result)
		print "Parallel Kmeans on 2D data set took " + str(end_time - start_time) + " second(s)"
	else:
		slave_function()	
