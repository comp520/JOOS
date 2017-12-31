# --------------- Pick one of these compilers,  
#JAVAC = javac 
JAVAC = joosc 

# ---------------  Pick one of these flag settings.
#FLAGS = 
FLAGS = -O

# ---------------- Pick one of these appletviewers
#APPLETVIEWER = appletviewer
APPLETVIEWER = jappletviewer

# -----------------------------------------------------------------------
all: Imagemap.class Animator.class Animator2.class

# -----------------------------------------------------------------------
# Show an image map, and respond to clicking on different areas
#   From Java in a Nutshell
Imagemap.class : Imagemap.java  ImagemapRectangle.java
	$(JAVAC) $(FLAGS) Imagemap.java  ImagemapRectangle.java

# How to run it.
Imagemap : Imagemap.class
	$(APPLETVIEWER) Imagemap.html

# -----------------------------------------------------------------------
# An example of animation from Java in A Nutshell
Animator.class : Animator.java AnimatorRun.java
	$(JAVAC) $(FLAGS) Animator.java  AnimatorRun.java

# How to run it.
Animator : Animator.class
	$(APPLETVIEWER) Animator.html

# -----------------------------------------------------------------------
# An example of animation from Deitel and Deitel 
Animator2.class : Animator2.java Animator2Run.java
	$(JAVAC) $(FLAGS) Animator2.java  Animator2Run.java

# How to run it.
Animator2 : Animator2.class
	$(APPLETVIEWER) Animator2.html

# -----------------------------------------------------------------------
clean	:  
	rm *.class *.j;
