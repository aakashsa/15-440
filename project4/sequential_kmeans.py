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
	Actual K Means Main Code 
'''	
def k_means (points,k):
	centroids = init_centroids(points,k)
	new_centroids = 	[]
	i  = 0
	while (True & i < 500):
		cluster_lists = assign_cluster(points,centroids,k)
		for list_item in cluster_lists:
			#print " LIST = ",list
			new_centroids.append(mean_average(list_item))
		print "\n Centroids     i = ",i , " ",centroids
		print "\n New Centroids i = ",i," ",new_centroids
		if (set(centroids) == set(new_centroids)):
			return centroids	
		centroids = copy.deepcopy(new_centroids)
		new_centroids = []
		i+=1
	return centroids	


print "K Means 1 = ",k_means([(100,100),(-100,-100),(100,-100),(-100,100),(9,900),(-900,900),(700,-900),(-900,-900),(10,10),(-10,10),(10,-10),(-10,-10)],4)

#print "K Means 2 = ",k_means([(0,0),(1,1),(2,2),(3,3),(4,4)],3)
print mean_average([(1,1),(2,2),(3,3)])
print assign_cluster([(10,10),(-10,10),(10,-10),(-10,-10)],[(5,5),(-5,5),(5,-5),(-5,-5)],4)
print init_centroids([1,2,3,4,5,6,7,8,9,10],2)
print "hEllo World"
print "distance of [3,6], [6,7]= ", euclidean_dist([3,6], [1,1])





