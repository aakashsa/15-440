# first argument = number of points to generate
# second argument = dimension of each point
# third argument = output file
dna_data:
	python dna_generator.py 500 5 text.txt

# first arg = points file
# second arg = number of centroids to pick
# third arg = output file
dna_centroids:
	python centroid_picker.py text.txt 10 dnacentroids.txt

# generate 2D centroids
centroids:
	python centroid_picker.py seq_kmeans_data1.txt 4 2dcentroids.txt

# first argument = input dna data file
# second argument = k
seq_DNAkmeans:
	python sequentialDNA_kmeans.py text.txt 5