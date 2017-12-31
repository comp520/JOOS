# --------------- Pick one of these compilers,  
#JAVAC = javac 
JAVAC = joosc 

# --------------- Pick one of these flag settings.
FLAGS = 
#FLAGS = -O

# -----------------------------------------------------------------------
all:	First1.class TicTacToe.class

# -----------------------------------------------------------------------
#  Our first WIG applet
First1.class : First1.java 
	$(JAVAC) $(FLAGS) First1.java 

# -----------------------------------------------------------------------
# Applet for the TicTacToe WIG
TicTacToe.class : TicTacToe.java 
	$(JAVAC) $(FLAGS) TicTacToe.java 

# -----------------------------------------------------------------------
clean	:  
	rm *.class *.j;
