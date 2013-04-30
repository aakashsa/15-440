

# first argument = number of points to generate
# second argument = dimension of each point
# third argument = output file
dna_data:
	python dna_generator.py 200000 10 dna_data.txt

# First Argyment = number of points to generate
# Second Argument = dimension of each file
# Third Argument = OutPut File
# Fourth Argument = Range of Grid to select points from - k means we are
# confined between (-k,-k) and (k,k)

2d_data:
	python 2d_generator.py 200000 2 2d_data.txt 400

# first arg = points file
# second arg = number of centroids to pick
# third arg = output file
dna_centroids:
	python centroid_picker.py dna_data.txt 10 dnacentroids.txt

# first arg = points file
# second arg = number of centroids to pick
# third arg = output file
2d_centroids:
	python centroid_picker.py 2d_data.txt 4 2dcentroids.txt

seq_2d_kmeans:
	python sequential_kmeans.py 2d_data.txt 4 2dcentroids.txt

seq_dna_kmeans:
	python sequential_dna_kmeans.py dna_data.txt 10 dnacentroids.txt

par_2d_kmeans:
	mpirun -n $(N) -machinefile machines_ghc.txt python parallel_kmeans.py 2d_data.txt 4 2dcentroids.txt

par_dna_kmeans:
	mpirun -n $(N) -machinefile machines_ghc.txt python parallel_dna_kmeans.py dna_data.txt 10 dnacentroids.txt

dna:
	make dna_data
	make dna_centroids
	make seq_dna_kmeans
	make par_dna_kmeans

2d:
	make 2d_data
	make 2d_centroids
	make seq_2d_kmeans
	make par_2d_kmeans