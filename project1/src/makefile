all:
	javac processmanagerstuff/*.java slavemanagerstuff/*.java processes/*.java interfaces/*.java
master:
	make clean 
	make all
	java processmanagerstuff/ProcessManager $(HOST)
slave: 
	java processmanagerstuff/ProcessManager -c $(HOST) $(PORT)
clean:
	find . -name "*.class" -delete
	
