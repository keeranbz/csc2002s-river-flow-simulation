# River Flow Simulation
In this assignment, a multithreaded Java program will be designed, ensuring both thread safety and sufficient concurrency for it to function well. 

The task is to implement a multithreaded water flow simulator that shows how water on a terrain flows downhill, accumulating in basins and flowing off the edge of the terrain.

### Run with Makefile:
Navigate to root directory in cli.

Compile:
```
make
```

Run:
```
make run
```

Clean generated files from directories:
```
make clean
```


### Run with Java:
Navigate to root directory in cli.

Compile:
```
javac src/*.java; mv src/*.class bin/;(cd docs/;javadoc ../src/*)
```

Run:
```
java -cp "bin/" Flow data/<DATA_INPUT_FILE>
```

Clean generated files from directories:
```
rm bin/*; rm -rf docs/*
```

