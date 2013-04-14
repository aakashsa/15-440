all:
	javac -cp json-simple-1.1.1.jar ./*/*.java

masterNode:
	#runjob shakespeare.txt.conv test/wordcount
	#runjob input.txt test/collating
	java -classpath .:json-simple-1.1.1.jar master/HadoopMaster Constants.json

worker1:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker 9056,9055

worker2:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker 9045,9046

worker:
	java -classpath .:json-simple-1.1.1.jar nodework/Worker $(PORTS)

clean:
	rm -r wordcount_*/
	rm -r collating_*/
