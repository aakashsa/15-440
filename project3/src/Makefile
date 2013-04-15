all:
	javac -cp json-simple-1.1.1.jar ./*/*.java

masterNode:
	#runjob words1.txt /afs/andrew.cmu.edu/...
	#runjob coll.txt /afs/andrew.cmu.edu/...
	java -classpath .:json-simple-1.1.1.jar master/HadoopMaster Constants.json

worker1:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker 9056,9055 Constants.json

worker2:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker 9045,9046 Constants.json

worker:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker $(PORTS) Constants.json

clean_word:
	rm -r wordcount_*/
clean_coll:	
	rm -r collating_*/
