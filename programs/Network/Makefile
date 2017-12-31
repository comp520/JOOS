# --------------- Pick one of these compilers,  
#JAVAC = javac 
JAVAC = joosc 

# --------------- Pick one of these flag settings.
#FLAGS = 
FLAGS = -O

# --------------- Pick one of these java commands
#JAVA = java
JAVA = jjava

# -----------------------------------------------------------------------
all:	ReverseURL.class TestURL.class

# -----------------------------------------------------------------------
# Simple example "calling" demo cgi script at sun.
ReverseURL.class : ReverseURL.java 
	$(JAVAC) $(FLAGS) ReverseURL.java 

# how to run it 
ReverseURL : ReverseURL.class
	$(JAVA) ReverseURL
 
# -----------------------------------------------------------------------
# Simple example "calling" demo cgi script at at cgi-mis at brics.
TestURL.class : TestURL.java 
	$(JAVAC) $(FLAGS) TestURL.java 

# how to run it 
TestURL : TestURL.class
	$(JAVA) TestURL
 
# -----------------------------------------------------------------------
clean	:  
	rm *.class *.j;
