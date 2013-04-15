all:
	javac -cp json-simple-1.1.1.jar ./*/*.java

masterNode:
	#runjob shakespeare.txt.conv test
	#runjob coll.txt test2
	java -classpath .:json-simple-1.1.1.jar master/HadoopMaster Constants.json

worker1:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker 9056,9055

worker2:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker 9045,9046

worker:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker $(PORTS) Constants.json

clean_word:
	rm -r wordcount_*/
clean_coll:	
	rm -r collating_*/
