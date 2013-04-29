import random
import sys
import copy

'''
	This Function Computes the similarity between two DNA points which are equal length vectors.	
'''
def similarity_dist(point1, point2):
	sim =  len(point1)
	for i in range(0,len(point1)):
		if (point1[i] != point2[i]):
			sim-= 1
	return sim

#print "similarity_dist 1 = ",similarity_dist(('a','c','g','t'),('a','c','g','t'))
#print "similarity_dist 2 = ",similarity_dist(('a','c','g','t'),('a','c','g','g'))


'''
	Given a List of Points it picks k unique random centroids
        points = list of data points
        k = integer
        return: list of centroids/points that are chosen as centroids
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
#print "Init Centroids = ",init_centroids([1,2,3,4,5,6,7,8,9,10],2)



'''
	Find the closest similar centroid index to a given point from a given list of centroids
        point = point to find closest centroid for
        centroids = list of centroids to consider
        return: index of centroid that the point is closest to
'''
def find_similar_centroid(point,centroids):
	max_dist = -sys.maxint - 1
	cur_centroid = 0
	for i in range(0,len(centroids)):
		icentroid_dist = similarity_dist(point,centroids[i])
		if (icentroid_dist > max_dist):
			max_dist = icentroid_dist
			cur_centroid = i
	return cur_centroid


#print "closest Point 1 = ",find_similar_centroid(('a','c','g','t'),[('a','c','g','t'),('a','a','a','a')])

				
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

#print "Assign Cluster 1 = ",assign_cluster( [('a','c','g','t'),('a','a','a','a'),('a','a','a','a'),('a','a','a','a')] ,[('a','c','g','t'),('a','a','a','a')],2)

'''
	New Main Average Points of the given points
'''
def new_centroid(points):
        if (len(points)==0):
                return
        dimension = len(points[0])
        answer = []
        for i in range(0, dimension):
                # Most Frequenct
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
        return answer	

#print "New Centroid = ", new_centroid([('a','c','g','t'),('a','a','a','a'),('a','a','a','a')])

'''
	Find the closest Point to a given point from a given list of points
        point = point in consideration
        points = points to look through
        return: point that is closest to this point
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

#print "K Means 2 = ",k_means([('a','a','a','a'),('a','a','a','g'),('t','t','t','c'),('t','t','t','t')],2)
#print mean_average([(1,1),(2,2),(3,3)])
#print assign_cluster([(10,10),(-10,10),(10,-10),(-10,-10)],[(5,5),(-5,5),(5,-5),(-5,-5)],4)
#print init_centroids([1,2,3,4,5,6,7,8,9,10],2)
#print "hEllo World"
#print "distance of [3,6], [6,7]= ", euclidean_dist([3,6], [1,1])
if __name__ == "__main__":
        if (len(sys.argv) != 3):
                print "Usage: python sequentialDNA_kmeans.py <pointsFile> <k>"
                sys.exit(0)
        k = int(sys.argv[2])
        if (k <= 0):
                print "k must be at least 1"
                sys.exit(0)
        f = open(sys.argv[1])
        strPoints = [line.strip() for line in open(sys.argv[1])]
        points = []
        for pt in strPoints:
                points.append(tuple(pt.split(',')))

        if (k > len(points)):
                print "k must be at most the number of data points"
                sys.exit(0)

        # Start k means algorithm
        print k_means(points, k)
