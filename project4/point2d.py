import math
import sys
from exceptions import NotImplementedError

'''
	Class that represents a 2D point
'''
class Point2D:

	x = 0
	y = 0

	def __init__(self, x, y):
		self.x = x
		self.y = y

	def __str__(self):
		return '(' + str(self.x) + ', ' + str(self.y) + ')'

	def __add__(self, other):
		if (other is None):
			return Point2D(self.x, self.y)
		if not isinstance(other, Point2D):
			return NotImplementedError
		return Point2D(self.x + other.x, self.y + other.y)

	def __cmp__(self, other):
		if not isinstance(other, Point2D):
			return NotImplementedError
		return cmp(self.x, other.x) and cmp(self.y, other.y)

	def __hash__(self):
		return hash((self.x, self.y))

	def __len__(self):
		return 2

	def __getitem__(self, i):
		if (i == 0):
			return self.x
		if (i == 1):
			return self.y

	'''
		Function to compute euclidean distance bewteen two 2D points
	'''
	def distance(self, other):
		if not isinstance(other, Point2D):
			return NotImplementedError
		dx = math.fabs(self.x - other.x)
		dy = math.fabs(self.y - other.y)
		return math.sqrt(dx * dx + dy * dy)

	def scalarMultiply(self, scalar):
		return Point2D(self.x * scalar, self.y * scalar)

	def find_closest_point(self, points):
		if (len(points) == 0):
			return None
		min_dist = sys.maxint
		cur_centroid = 0
		for i in xrange(len(points)):
			icentroid_dist = self.distance(points[i])
			if (icentroid_dist <= min_dist):
				min_dist = icentroid_dist
				cur_centroid = i
		return cur_centroid

	@staticmethod
	def getAverage(points):
		if (len(points) == 0):
			return None
		sumx = 0
		sumy = 0
		for point in points:
			sumx += point.x
			sumy += point.y
		return Point2D(float(sumx)/len(points), float(sumy)/len(points))

	@staticmethod
	def stringify(points):
		strList = [str(pt) for pt in points]
		return '[' + ', '.join(strList) + ']'


## Some tests for this file
if __name__ == "__main__":
	p1 = Point2D(3,3)
	p2 = Point2D(3,4)
	p3 = Point2D(4,3)
	p4 = Point2D(4,4)
	assert(Point2D.getAverage([p1, p2, p3, p4]) == Point2D(3.5, 3.5))
	print Point2D.stringify([p1, p2, p3, p4])
	assert(p1.scalarMultiply(1.5) == Point2D(4.5, 4.5))
	assert(p1.euclideanDist(p4) == math.sqrt(2))
	assert(p1 + p2 == Point2D(6,7))