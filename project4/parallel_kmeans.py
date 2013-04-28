from mpi4py import *
import random
import sys
import copy
import operator

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
	answer =  [ [] for i in range(n) ]
	for i in range(0,len(points)):
		answer[(i%n)].append(points[i])
	return answer 	

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
# Test
#print "Mean Average = ",mean_average([(1,2),(8,2),(7,2),(4,2),,(3,2)])	

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
	if (point ==None):
			return None
	answer = [ 0 for i in range(len(point))]
	print " Point = ",point, " factor = ",factor	
	for i in range(len(point)):	
		answer[i] = point[i]*factor	
	return tuple(answer)
#Tested

'''
	Adding two Points as it is
'''
def add_points (point1, point2):	
	if point1==None:
		print "Point 1 is None"
		return point2
	if point2==None:
		print "Point 2 is None"
		return point1	
	return tuple(map(operator.add, point1, point2))

'''
	Centroids from all n-1 processess - centroid_local
	population from all n-1 processess - population_local
'''
def recalculate_centroid(centroid_local,population_local,k):
	print "Centroid_local ", centroid_local
	print "Population_local ", population_local

	total_size = [ 0 for i in range(k)]
	temp = 0 
	# Total Size of Each Cluster
	for i in range(0,k):
		for node in population_local:
			temp+= node[i]
		total_size[i] = temp
		temp = 0	
	# Total Size has total size of each of the k clusters
	# Initialize the Answer with the Weighed Centroids we got from Slave one
	answer =  []
	for i in range(0,k):
		print "Total_size[i]  = ",total_size[0]
		print "Float Total_size[i]  = ",float(total_size[0])
		if total_size[i]!=0:
			factored = multiply_factor(centroid_local[0][i],(float(population_local[0][i])/float(total_size[i])))
			print "Centroid Local[0][i] = ",centroid_local[0][i]
			print "Multiplied Point = ",factored
			answer.append(factored)
		else: 
			answer.append(centroid_local[0][i])

	# For Each Slaves Centroid List we have to weigh it with the population size in that cluster	
	for i in range(0,k):
		for j in range(1,len(centroid_local)):
			print "Total_size[i]  = ",total_size[i]
			print "Float Total_size[i]  = ",float(total_size[i])
			
			if (total_size[i]!=0):
				factored = multiply_factor(centroid_local[j][i],(float(population_local[j][i])/float(total_size[i])))
				print "Centroid Local[0][i] = ",centroid_local[0][i]
				print "Multiplied Point = ",factored
				answer[i] = add_points(answer[i],factored)
			print " Recalculate answer ",answer	
	return answer		

'''
	Master Functionality Main K_means Code
'''
def master_function(points,k):
	# Initial Centroids
	#centroids = init_centroids(points,k)
	centroids = [(3,3),(-3,3),(-3,-3),(3,-3)]
	comm = MPI.COMM_WORLD
	#Rank of This Processor
	rank = comm.Get_rank()
	if rank!=0:
		return
	size = comm.Get_size()
	# Partition The points for the slaves
	partition = partition_points(points,size-1)

	centroid_slave =  [ [] for i in range(size-1) ]
	population_slave =   [ [] for i in range(size-1) ]
	count = 0
	for i in range (1,size):
		# Send the Data Points to the Slaves
	   comm.send(partition[i-1], dest = i)
	while (True  or count <50):
		# Send the Initial Centroids
		comm.bcast(centroids)   		
		for i in range (1,size):
			# Receive their centroids and population count for ( EACH cluster of centroid )
	   		(centroid_slave[i-1],population_slave[i-1]) = comm.recv(source = i)

		# Recalculating the New Centroids Based on the Global FeedBack
		new_centroids = recalculate_centroid(centroid_slave,population_slave,k)
		print " Old Centroids =",centroids
		print " New Centroids =",new_centroids

		# If Centroids Haven't Changed then We are done and we send empty list to slaves to signal end
		if (set(centroids) == set(new_centroids)):
	   		comm.bcast([],root=0)
			return centroids	
		# New Centroid Copy
		centroids = copy.deepcopy(new_centroids)
		new_centroids = []
		count+=1
		print "\n Count Updated \n"
	return centroids	

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
	print " Received Points = ",data_points
	# New Centroid List
	new_centroid =  [ [] for i in range(k) ]
	# Population List have size of each cluster
	population_slave = [ [] for i in range(k) ]
	centroids = []
	while (True):
		# Receiving the Centroids
		centroids = comm.bcast(centroids,root=0) 
		print"Received Centroids ",centroids
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


if __name__ == "__main__":
	if (len(sys.argv) != 3):
    		print "Usage: python parallel_kmeans.py <pointsFile> <k>"
    		sys.exit(0)

	k = int(sys.argv[2])


	if (k <= 0):
		print "k must be at least 1"
		sys.exit(0)

	f = open(sys.argv[1])
	strPoints = [line.strip() for line in open(sys.argv[1])]
	points = []
	for pt in strPoints:
		list_strings = pt.split(',')
		list_int = [int(elem) for elem in list_strings]    
		points.append(tuple(list_int))

	if (k > len(points)):
		print "k must be at most the number of data points"
		sys.exit(0)

        # Start k means algorithm
#    print k_means(points, k)
	comm = MPI.COMM_WORLD
	rank = comm.Get_rank()
	print " Rank = ",rank
	print " Points = ",points
	#points = [(100,100),(-100,-100),(100,-100),(-100,100),(9,900),(-900,900),(700,-900),(-900,-900),(10,10),(-10,10),(10,-10),(-10,-10)]
	if rank == 0:
		answer = master_function(points,k)
		print " Fina Answer = ",answer
	else:
		slave_function(k)	








