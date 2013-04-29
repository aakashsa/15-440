from exceptions import NotImplementedError
from collections import defaultdict
import sys

class PointDNA:
	dna = []

	def __init__(self, dnaList):
		self.dna = dnaList

	def __str__(self):
		return '(' + ', '.join(self.dna) + ')'

	def __len__(self):
		return len(self.dna)

	def __getitem__(self, i):
		return self.dna[i]

	def __hash__(self):
		return hash(tuple(self.dna))

	def __cmp__(self, other):
		if not isinstance(other, PointDNA):
			return NotImplementedError
		b = True
		for i in xrange(len(self)):
			b = b and cmp(self[i], other[i])
		return b

	def distance(self, other):
		if not isinstance(other, PointDNA):
			return NotImplementedError
		if (len(self) != len(other)):
			return None
		sim = len(self)
		for i in range(0, len(self)):
			if (self[i] != other[i]):
				sim-= 1
		return sim

	def find_closest_point(self, points):
		if (len(points) == 0):
			return None
		max_dist = -sys.maxint - 1
		cur_centroid = 0
		for i in xrange(len(points)):
			icentroid_dist = self.distance(points[i])
			if (icentroid_dist > max_dist):
				max_dist = icentroid_dist
				cur_centroid = i
		return cur_centroid

	@staticmethod
	def stringify(points):
		strList = [str(pt) for pt in points]
		return '[' + ', '.join(strList) + ']'

## Tests
if __name__ == "__main__":
	dna1 = PointDNA(['a','c','g','t'])
	dna2 = PointDNA(['a','c','g','t'])
	assert(dna1.distance(dna2) == 4)
	dna3 = PointDNA(['a','c','g','g'])
	assert(dna1.distance(dna3) == 3)
