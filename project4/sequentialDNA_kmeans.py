import random
import sys
import copy


'''
	This Function Computes the similarity between two DNApoint1 , DNApoint2 which are equal length vectors.	
'''
def similarity_dist(point1,point2):
	sim =  len(point1)
	for i in range(0,len(point1)):
		if (point1[i]!=point2[i]):
			sim-= 1
	return sim

print "similarity_dist 1 = ",similarity_dist(('a','c','g','t'),('a','c','g','t'))
print "similarity_dist 2 = ",similarity_dist(('a','c','g','t'),('a','c','g','g'))


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
print "Init Centroids = ",init_centroids([1,2,3,4,5,6,7,8,9,10],2)



'''
	Find the closest similar centroid index to a given point from a given list of centroids
'''
def find_similar_centroid(point,centroids):
	max_dist = -sys.maxint - 1
	cur_centroid = 0
	for i in range(0,len(centroids)):
		icentroid_dist = similarity_dist(point,centroids[i])
		if (icentroid_dist > max_dist):
			max_dist = icentroid_dist
			cur_centroid = i
	#print " 49 answer", cur_centroid 
	return cur_centroid


print "closest Point 1 = ",find_similar_centroid(('a','c','g','t'),[('a','c','g','t'),('a','a','a','a')])

				
'''
	Given a List of Points it returns k lists where each list is the 
	bunch of points corresponding to a 
'''	
def assign_cluster(points,centroids,k):
	if (len(centroids)!=k):
		print "EXCEPTION PROBLEM"	
	answer =  [ [] for i in range(0,k) ]
	for point in points:
		new_assignment = find_similar_centroid(point,centroids)
#		print " centroids 61 = ",centroids
#		print " Point = ",point, " new_assignment  = ", new_assignment
#		print "Answer = ",answer
		answer[new_assignment].append(point)	
		#print " Answer for Point = ",point, " = ", answer
	return answer		

print "Assign Cluster 1 = ",assign_cluster( [('a','c','g','t'),('a','a','a','a'),('a','a','a','a'),('a','a','a','a')] ,[('a','c','g','t'),('a','a','a','a')],2)

'''
	New Main Average Points of the given points
'''
def new_centroid(points):
		if (len(points)==0):
			return
		dimension = len(points[0])
		# a, c , g , t mapping
		answer = []
		chars = ['a','c','g','t']
		temp = [0,0,0,0]
		#temp_sum =  [ [0] for i in range(dimension) ]
		for i in range(0,dimension):
			# Most Frequenct
			for point in points:
				if (point[i] == 'a'):
					temp[0]+=1	
				if (point[i]=='c'):
					temp[1]+=1
				if (point[i]=='g'):
					temp[2]+=1
				if (point[i]=='t'):
					temp[3]+=1
			#print "temp = ",temp
			max_i = 0
			max_count = temp[0]
			for i in range(0,4):
				if (temp[i]>=max_count):
					max_count = temp[i]
					max_i = i
			temp = [0,0,0,0]
			answer.append(chars[max_i])		
		return answer	

print "New Centroid = ", new_centroid([('a','c','g','t'),('a','a','a','a'),('a','a','a','a')])

'''
	Find the closest Point to a given point from a given list of points
'''
def find_similar_point(point,points):
	max_dist = -sys.maxint - 1
	cur_points = 0
	for i in range(0,len(points)):
		ipoints_dist = similarity_dist(point,points[i])
		if (ipoints_dist > max_dist):
			max_dist = ipoints_dist
			cur_points = i
	return points[cur_points]

	
'''
	Actual K Means Main Code 
'''
def k_means (points,k):
	centroids = init_centroids(points,k)
	#print "\n Initial Centroids ", centroids
	new_centroid_list = []
	i  = 0
	while (True & i < 500):
		cluster_lists = assign_cluster(points,centroids,k)
		#print "Clouster List = ",cluster_lists
		for list_item in cluster_lists:
			#print " LIST ITEM = ",list_item
			#print "new_cent",new_centroid_list
			#print "new_centroids = ", new_centroid(list_item)
			#print "points = ", points
			#print "find_similar_point(new_centroid(list_item),points = ",find_similar_point(new_centroid(list_item),points)			
			new_centroid_list.append(find_similar_point(new_centroid(list_item),points))
		#print "\n Centroids     i = ",i , " ",centroids
		#print "\n New Centroids i = ",i," ",new_centroid_list
		if (set(centroids) == set(new_centroid_list)):
			return centroids
		centroids = copy.deepcopy(new_centroid_list)
		new_centroid_list = []
		i+=1
	return centroids

print "K Means 2 = ",k_means([('a','a','a','a'),('a','a','a','g'),('t','t','t','c'),('t','t','t','t')],2)
#print mean_average([(1,1),(2,2),(3,3)])
#print assign_cluster([(10,10),(-10,10),(10,-10),(-10,-10)],[(5,5),(-5,5),(5,-5),(-5,-5)],4)
#print init_centroids([1,2,3,4,5,6,7,8,9,10],2)
#print "hEllo World"
#print "distance of [3,6], [6,7]= ", euclidean_dist([3,6], [1,1])

