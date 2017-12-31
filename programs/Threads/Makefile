# --------------- Pick one of these compilers,  
#JAVAC = javac
JAVAC = joosc 

# --------------- Pick one of these flag settings.
#FLAGS = 
FLAGS = -O

# ---------------- Pick one of these java commands
#JAVA = java
JAVA = jjava

# -----------------------------------------------------------------------
all:	PrintTest.class SharedCell.class  SharedCellSync.class

# -----------------------------------------------------------------------
# Simple example of starting 4 threads 
PrintTest.class : PrintTest.java PrintThread.java
	$(JAVAC) $(FLAGS) PrintTest.java PrintThread.java

# how to run it 
PrintTest : PrintTest.class
	$(JAVA) PrintTest
 
# -----------------------------------------------------------------------
# Producer and Consumer that are not synchronized
SharedCell.class : SharedCell.java ProduceInteger.java \
		   ConsumeInteger.java HoldInteger.java 
	$(JAVAC) $(FLAGS) SharedCell.java ProduceInteger.java \
			  ConsumeInteger.java HoldInteger.java 

# how to run it 
SharedCell : SharedCell.class
	$(JAVA) SharedCell
 
# -----------------------------------------------------------------------
# Producer and Consumer that are synchronized
SharedCellSync.class : SharedCellSync.java ProduceIntegerSync.java \
		   ConsumeIntegerSync.java HoldIntegerSync.java 
	$(JAVAC) $(FLAGS) SharedCellSync.java ProduceIntegerSync.java \
			  ConsumeIntegerSync.java HoldIntegerSync.java 

# how to run it 
SharedCellSync : SharedCellSync.class
	$(JAVA) SharedCellSync
 
# -----------------------------------------------------------------------
clean	:  
	rm *.class *.j;
