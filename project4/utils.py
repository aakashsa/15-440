import random
import sys
from point2d import Point2D
from pointdna import PointDNA

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
		new_assignment = point.find_closest_point(centroids)
		assignments[new_assignment].append(point)	
	return assignments

'''
	Read 2D points from the given file
'''
def read_2D_points(pointsFile):
	strPoints = [line.strip() for line in open(pointsFile)]
	if (len(strPoints) == 0):
		print "2D points file is empty"
		sys.exit(0)
	points = []
	for s in strPoints:
		list_str = s.split(',')
		points.append(Point2D(int(list_str[0]), int(list_str[1])))
	return points

'''
	Read DNA points from the given file
'''
def read_DNA_points(pointsFile):
	strPoints = [line.strip() for line in open(pointsFile)]
	if (len(strPoints) == 0):
		print "DNA points file is empty"
		sys.exit(0)
	points = []
	for s in strPoints:
		points.append(PointDNA(s.split(',')))
	return points
