all:
	javac registryROR/*.java
registry:
	make clean 
	make all
	java registryROR/RMIRegistry440Server $(PORT)
rem:
	java registryROR/SampleRemServer400
foo: 
	java registryROR/SampleFooServer440
servers:
	java registryROR/SampleRemServer400 &
	java registryROR/SampleFooServer440 &
demo:
	javac registryROR/AllTests.java
	java registryROR/AllTests
same:
	java registryROR/CallingSameObjectTest
clean:
	find . -name "*.class" -delete