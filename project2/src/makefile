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
demo:
	java registryROR/ProxyDemo
clean:
	find . -name "*.class" -delete
			

