all:
	javac processmanagerstuff/*.java slavemanagerstuff/*.java processes/*.java interfaces/*.java
master:
	make clean 
	make all
	java processmanagerstuff/ProcessManager
slave: 
	java processmanagerstuff/ProcessManager -c localhost
clean:
	find . -name "*.class" -delete
	
