all:
	javac processmanagerstuff/*.java slavemanagerstuff/*.java processes/*.java interfaces/*.java

master:
	java processmanagerstuff/ProcessManager3 1234
slave: 
	java processmanagerstuff/ProcessManager3 -c localhost 1234
clean:
	find . -name "*.class" -delete
	
