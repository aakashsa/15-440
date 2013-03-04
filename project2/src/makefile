all:
	javac rmi440/*/*.java
	javac rmi440/*/*/*.java

registry:
	make clean 
	make all
	java rmi440/registrycode/RMIRegistry440Server $(PORT)

rem:
	java rmi440/tests/server/SampleRemServer400

foo: 
	java rmi440/tests/server/SampleFooServer440

demo:
	javac rmi440/tests/client/AllTests.java
	javac rmi440/tests/client/CallingSameObjectTest.java
	java rmi440/tests/client/AllTests
	java rmi440/tests/client/CallingSameObjectTest

clean:
	find . -name "*.class" -delete