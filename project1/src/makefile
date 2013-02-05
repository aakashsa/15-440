all:
	javac processmanagerstuff/*.java slavemanagerstuff/*.java processes/*.java interfaces/*.java
master:
	make clean 
	make all
	java processmanagerstuff/ProcessManager3
slave: 
	java processmanagerstuff/ProcessManager3 -c localhost
clean:
	find . -name "*.class" -delete
	
