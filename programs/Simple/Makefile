# Pick one of these compile methods.  use extra j commands if CLASSPATH not set
# JAVAC = jjavac 
# JAVAC = javac 
# JAVAC = jjoosc
JAVAC = joosc 

# Pick one of these flag settings.
FLAGS = 
#FLAGS = -O

# -----------------------------------------------------------------------
all:	Hello.class TestIO.class  TestString.class TestIOlist.class \
	UseCons.class TestShortCircuit.class UseExtCons.class TestComp.class \
	StrCat.class UseBox.class UseBenchmark.class \
	TestForInc.class TestNull.class ShowRuntime.class

# -----------------------------------------------------------------------
# basic hello world test
Hello.class : Hello.java
	$(JAVAC) $(FLAGS) Hello.java

# -----------------------------------------------------------------------
# test the basic IO ops defined in JoosIO
TestIO.class : TestIO.java
	$(JAVAC) $(FLAGS) TestIO.java 

# -----------------------------------------------------------------------
# test the string ops that are used to convert strings to
#                     integers, booleans, or back to Strings
TestString.class : TestString.java
	$(JAVAC) $(FLAGS) TestString.java 

# -----------------------------------------------------------------------
# test basic JoosIO ops + java's StringTokenizer
TestIOlist.class : TestIOlist.java
	$(JAVAC) $(FLAGS) TestIOlist.java 

# -----------------------------------------------------------------------
# test handling of short circuit logical ops
TestShortCircuit.class	:	TestShortCircuit.java
	$(JAVAC) $(FLAGS) TestShortCircuit.java

# -----------------------------------------------------------------------
# test recursive class
UseCons.class : UseCons.java Cons.java
	$(JAVAC) $(FLAGS) UseCons.java Cons.java 

# -----------------------------------------------------------------------
# test extending a class
UseExtCons.class : UseExtCons.java Cons.java ExtCons.java
	$(JAVAC) $(FLAGS) UseExtCons.java Cons.java ExtCons.java

# -----------------------------------------------------------------------
# test type checking in java, instanceof, casting ...
#    lines commented out in this test should give syntax errors
TestComp.class	: TestComp.java Tree.java Cons.java ExtCons.java
	$(JAVAC) $(FLAGS) TestComp.java Tree.java Cons.java ExtCons.java

# -----------------------------------------------------------------------
# illustrates how StrCat works
StrCat.class	: StrCat.java
	$(JAVAC) $(FLAGS) StrCat.java

# -----------------------------------------------------------------------
# test synchronized methods
UseBox.class :	UseBox.java SyncBox.java
	$(JAVAC) $(FLAGS) UseBox.java SyncBox.java

# -----------------------------------------------------------------------
# test abstract methods and classes, also an example of Benchmarking
UseBenchmark.class :	UseBenchmark.java Benchmark.java ExtBenchmark.java
	$(JAVAC) $(FLAGS) UseBenchmark.java Benchmark.java ExtBenchmark.java

# -----------------------------------------------------------------------
# test for loop and i++
TestForInc.class : TestForInc.java
	$(JAVAC) $(FLAGS) TestForInc.java

# -----------------------------------------------------------------------
# test printing of null
TestNull.class : TestNull.java
	$(JAVAC) $(FLAGS) TestNull.java

# -----------------------------------------------------------------------
# Illustrate a runtime exception
ShowRuntime.class : ShowRuntime.java
	$(JAVAC) $(FLAGS) ShowRuntime.java

# -----------------------------------------------------------------------
clean	:  
	rm *.class *.j;
