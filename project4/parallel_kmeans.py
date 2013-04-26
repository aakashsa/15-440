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


def add_points (points):
	answer = []
	temp = 0
	for i in range(len(point[0])):
		for point in points:
			temp+= point[i] 
		answer.append(temp)	
	return answer 

'''
	Centroids from all n-1 processess - centroid_local
	population from all n-1 processess - population_local
	total number of points in the whole set up - size
'''
def recalculate_centroid(centroid_local,population_local,size):
	answer =  [ [] for i in range(0,len(centroid_local)) ]
	temp = []
	for j in len(0,centroid_local[0]):
		for i in range(0,len(centroid_local)):
			temp+= centroid_local[i][j]* (float(population_local[i])/float(size))  
	answer[i].append(temp)



'''
	Master Functionality Main K_means Code
'''
def master_function(points,k):
	centroids = init_centroids(points,k)
	comm = MPI.COMM_WORLD
	rank = comm.Get_rank()
	size = comm.Get_size()
	partition = partition_points(points,k-1)
	centroid_local = 	answer =  [ [] for i in range(size) ]
	population_local = 	answer =  [ [] for i in range(size) ]
	count = 0
	for i in range (1,size):
	   comm.send(partition[i], dest=i)
	while (True  or count <50):   
		comm.bcast(centroids,root=0)   		
		for i in range (1,size):
	   		(centroid_local[i],population_local[i]) = comm.recv(source = i)
	   	new_centroids = recalculate_centroid(centroid_local,population_local,len(points))	
	   	if (set(centroids) == set(new_centroids)):
	   		comm.bcast([],root=0)
			return centroids	
		centroids = copy.deepcopy(new_centroids)
		new_centroids = []
		count+=1

'''
	Slave Functionality Main K_means Code
'''
def slave_function(k):
	comm = MPI.COMM_WORLD
	rank = comm.Get_rank()
	if rank ==0:
		return 	
	data_points = comm.recv(source = 0)
	new_centroid =  [ [] for i in range(k) ]
	while (True):
		centroids = comm.recv(source = 0)
		if len(centroids) == 0:
			return 
		assigned_points = assign_cluster(data_points,centroids,k)
		for i in range (0,k):
			new_centroid[i] = mean_average(data_points)
	   	comm.send((new_centroid,len(data_points)), dest=0)


def main():
	comm = MPI.COMM_WORLD
	rank = comm.Get_rank()
	if rank == 0:
		master_function(points,k)
	else
		slave_function(k)	














