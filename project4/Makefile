NUM_DNA_POINTS_PER_CLUSTER = 20000
NUM_2D_POINTS_PER_CLUSTER = 50000
DNA_K = 10
DNA_DIM = 10
2D_K = 4
2D_RANGE = 400

# first arg = number of points per cluster to generate
# second arg = dimension of each point
# third arg = number of clusters
# fourth argument = centroids output file
# fifth arg = points output file
dna_data:
	python dna_generator.py $(NUM_DNA_POINTS_PER_CLUSTER) $(DNA_DIM) $(DNA_K) dna_centroids.txt dna_data.txt

# first Argument = number of points to generate per cluster
# second Argument = max value of radius
# third Argument = number of clusters
# fourth Argument = output of cluster centroids
# fifth arg = output of points

2d_data:
	python 2d_generator.py $(NUM_2D_POINTS_PER_CLUSTER) $(2D_RANGE) $(2D_K) 2d_centroids.txt 2d_data.txt

seq_2d_kmeans:
	python sequential_kmeans.py 2d_data.txt $(2D_K) 2d_centroids.txt

seq_dna_kmeans:
	python sequential_dna_kmeans.py dna_data.txt $(DNA_K) dna_centroids.txt

par_2d_kmeans:
	mpirun -np $(N) -hostfile machines_ghc.txt python parallel_kmeans.py 2d_data.txt $(2D_K) 2d_centroids.txt

par_dna_kmeans:
	mpirun -np $(N) -hostfile machines_ghc.txt python parallel_dna_kmeans.py dna_data.txt $(DNA_K) dna_centroids.txt