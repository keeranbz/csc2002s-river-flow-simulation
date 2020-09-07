

.PHONY: run

compile: 
	javac src/*.java
	mv src/*.class bin/
	(cd docs/;javadoc ../src/*)

default: compile

run: 
	java bin/TerrainClassification


clean: 
	rm bin/*
	rm -rf docs/*

	
