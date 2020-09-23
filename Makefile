

.PHONY: run

compile: 
	javac src/*.java
	mv src/*.class bin/
	(cd docs/;javadoc ../src/*)

default: compile

run: 
	java -cp "bin/" Flow data/medsample_in.txt


clean: 
	rm bin/*
	rm -rf docs/*
	

	
