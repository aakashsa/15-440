all:
	make clean
	javac -cp json-simple-1.1.1.jar ./*/*.java

masterNode:
	java -classpath .:json-simple-1.1.1.jar master/HadoopMaster test/Constants.json

worker1:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker 9056 9055

worker2:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker 9045 9046

clean:
	rm -r final_answers/
	rm -r wordcount_worker/
	rm -r wordcount_partition/
