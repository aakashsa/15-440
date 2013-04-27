from mpi4py import *
import random
import sys
import copy

'''
	This Function Computes the distance between two point1 , point2
	which are equal length vectors.	
'''
def euclidean_dist(point1,point2):
	sum = 0.0
	for i in range(0,len(point1)):
		sum+= (point1[i]-point2[i])**2
	return sum**(0.5)	


'''
	Given a List of Points it picks a bunch of k unique random centroids
'''
def init_centroids(points,k):
	i = 0
	index = []
	centroids = []
	while (i<k):
		new_num = random.randrange(0,len(points))
		if new_num not in index:
			index.append(new_num)
			centroids.append(points[new_num])
			i+=1
	return centroids

'''
	Find the closest centroid index to a given point from a given list of centroids
'''
def find_closest_centroid(point,centroids):
	min_dist = sys.maxint
	cur_centroid = 0
	for i in range(0,len(centroids)):
		icentroid_dist = euclidean_dist(point,centroids[i])
		if (icentroid_dist < min_dist):
			min_dist = icentroid_dist
			cur_centroid = i
	return cur_centroid


'''
	Find the closest Point to a given point from a given list of points
'''
def find_closest_point(point,points):
	min_dist = sys.maxint
	cur_points = 0
	for i in range(0,len(points)):
		ipoints_dist = euclidean_dist(point,points[i])
		if (ipoints_dist < min_dist):
			min_dist = ipoints_dist
			cur_points = i
	return points[cur_points]


'''
	Given a set of points partition them into a set of n subset lists
'''
def partition_points(points,n):
	partition =  [ [] for i in range(n) ]
	for i in range(0,len(points)):
		answer[(i%n)].append(points[i])


'''
	New Main Average Points of the given points
'''
def mean_average(points):
		if (len(points)==0):
			return
		dimension = len(points[0])
		answer =  ()
		#temp_sum =  [ [0] for i in range(dimension) ]
		for i in range(0,dimension):
			temp = 0
			for point in points:
				temp+= point[i]
			answer = answer + (temp/len(points),)	
		return answer	


'''
	Given a List of Points it returns k lists where each list is the 
	bunch of points corresponding to a 
'''	
def assign_cluster(points,centroids,k):	
	answer =  [ [] for i in range(k) ]
	for point in points:
		new_assignment = find_closest_centroid(point,centroids)
		#print " Point = ",point, " new_assignment  = ", new_assignment
		answer[new_assignment].append(point)	
		#print " Answer for Point = ",point, " = ", answer
	return answer


'''
	 Multiply Vector by Factor 
'''
def multiply_factor(point,factor):
	for i in range(point):
		point[i] = point[i]*factor	
	return point


'''
	Adding two Points as it is
'''
def add_points (points):
	answer = ()
	temp = 0
	for i in range(len(point[0])):
		for point in points:
			temp+= point[i] 
		answer = answer + (temp/len(points),)	
	return answer


'''
	Centroids from all n-1 processess - centroid_local
	population from all n-1 processess - population_local
'''
def recalculate_centroid(centroid_local,population_local,k):
	total_size = [ 0 for i in range(K)]
	temp = 0
	# Total Size of Each Cluster
	for i in range(k):
		for node in population_local:
			temp+= node[i]
		total_size[i] = temp
		temp = 0	
	# Initialize the Answer with the Weighed Centroids we got from Slave one
	answer =  []
	for i in range(0,k):
		answer.append(multiply_factor(centroid_local[0][i],(float(population_local[0][i])/float(total_size[i]))))  
	# For Each Slaves Centroid List we have to weigh it with the population size in that cluster	
	for i in range(0,k):
		for j in range(1,len(centroid_local)):
			answer[i] = add_points(answer[i],multiply_factor(centroid_local[j][i],float(population_local[j][i])/float(total_size[i])))
	return answer		

'''
	Master Functionality Main K_means Code
'''
def master_function(points,k):
	# Initial Centroids
	centroids = init_centroids(points,k)
	comm = MPI.COMM_WORLD
	#Rank of This Processor
	rank = comm.Get_rank()
	if rank!=0:
		return
	size = comm.Get_size()
	# Partition The points for the slaves
	partition = partition_points(points,size-1)


	centroid_slave =  [ [] for i in range(size) ]
	population_slave =   [ [] for i in range(size) ]
	count = 0
	for i in range (1,size):
		# Send the Data Points to the Slaves
	   comm.send(partition[i-1], dest=i)
	while (True  or count <50): 
		# Send the Initial Centroids
		comm.bcast(centroids,root=0)   		
		for i in range (1,size):
			# Receive their centroids and population count for ( EACH cluster of centroid )
	   		(centroid_slave[i],population_slave[i]) = comm.recv(source = i)
		# Recalculating the New Centroids Based on the Global FeedBack
		new_centroids = recalculate_centroid(centroid_slave,population_slave,k)
		# If Centroids Haven't Changed then We are done and we send empty list to slaves to signal end
		if (set(centroids) == set(new_centroids)):
	   		comm.bcast([],root=0)
			return centroids	
		# New Centroid Copy
		centroids = copy.deepcopy(new_centroids)
		new_centroids = []
		count+=1

'''
	Slave Functionality Main K_means Code
'''
def slave_function(k):
	#Rank of This Processor
	comm = MPI.COMM_WORLD
	rank = comm.Get_rank()
	if rank ==0:
		return 	
	# Get Data Points	
	data_points = comm.recv(source = 0)
	# New Centroid List
	new_centroid =  [ [] for i in range(k) ]
	# Population List have size of each cluster
	population_slave = [ [] for i in range(k) ]
	while (True):
		# Receiving the Centroids
		centroids = comm.recv(source = 0)
		if len(centroids) == 0:
			return 
		# Assign Points to the clusters 
		assigned_points = assign_cluster(data_points,centroids,k)
		for i in range (0,k):
			# New Centroid For each cluster and its population Count
			new_centroid[i] = mean_average(assigned_points[i])
			population_slave[i] =  len (assigned_points[i])
		# Sending the New Centroid And the Population Count of Each Cluster	
	   	comm.send((new_centroid,population_slave), dest=0)


def main():
	comm = MPI.COMM_WORLD
	rank = comm.Get_rank()
	if rank == 0:
		master_function(points,k)
	else:
		slave_function(k)	














