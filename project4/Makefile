NUM_DNA_POINTS = 200000
NUM_2D_POINTS = 200000
DNA_K = 10
DNA_DIM = 10
2D_K = 4
2D_RANGE = 400

# first argument = number of points to generate
# second argument = dimension of each point
# third argument = output file
dna_data:
	python dna_generator.py $(NUM_DNA_POINTS) $(DNA_DIM) dna_data.txt

# First Argyment = number of points to generate
# Second Argument = dimension of each file
# Third Argument = OutPut File
# Fourth Argument = Range of Grid to select points from - k means we are
# confined between (-k,-k) and (k,k)

2d_data:
	python 2d_generator.py $(NUM_2D_POINTS) 2 2d_data.txt $(2D_RANGE)

# first arg = points file
# second arg = number of centroids to pick
# third arg = output file
dna_centroids:
	python centroid_picker.py dna_data.txt $(DNA_K) dnacentroids.txt

# first arg = points file
# second arg = number of centroids to pick
# third arg = output file
2d_centroids:
	python centroid_picker.py 2d_data.txt $(2D_K) 2dcentroids.txt

seq_2d_kmeans:
	python sequential_kmeans.py 2d_data.txt $(2D_K) 2dcentroids.txt

seq_dna_kmeans:
	python sequential_dna_kmeans.py dna_data.txt $(DNA_K) dnacentroids.txt

par_2d_kmeans:
	mpirun -np $(N) -hostfile machines_ghc.txt python parallel_kmeans.py 2d_data.txt $(2D_K) 2dcentroids.txt

par_dna_kmeans:
	mpirun -np $(N) -hostfile machines_ghc.txt python parallel_dna_kmeans.py dna_data.txt $(DNA_K) dnacentroids.txt