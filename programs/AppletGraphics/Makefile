# --------------- Pick one of these compilers,  
#JAVAC = javac 
JAVAC = joosc 


# ---------------  Pick one of these flag settings.
#FLAGS = 
FLAGS = -O

# --------------- Pick one of these appletviewers
APPLETVIEWER = jappletviewer
# APPLETVIEWER = appletviewer

# -----------------------------------------------------------------------
all: DemoFont.class DemoFont2.class DrawArcs.class DrawPoly.class \
	 MyLabel2.class Rectangle2.class ShowColors.class

# -----------------------------------------------------------------------
# Show different fonts
DemoFont.class : DemoFont.java 
	$(JAVAC) $(FLAGS) DemoFont.java 

# How to run it. (Can also browse DemoFont.html via netscape)
DemoFont : DemoFont.class
	$(APPLETVIEWER) DemoFont.html

# -----------------------------------------------------------------------
# Show more ops on fonts 
DemoFont2.class : DemoFont2.java 
	$(JAVAC) $(FLAGS) DemoFont2.java 

# How to run it.  (can also browse DemoFont2.html via netscape)
DemoFont2 : DemoFont2.class
	$(APPLETVIEWER) DemoFont2.html

# -----------------------------------------------------------------------
# Draw some arcs 
DrawArcs.class : DrawArcs.java 
	$(JAVAC) $(FLAGS) DrawArcs.java 

# How to run it.  (can also brwose DrawArcs.html via netscape)
DrawArcs : DrawArcs.class
	$(APPLETVIEWER) DrawArcs.html

# -----------------------------------------------------------------------
# Draw some polygons
DrawPoly.class : DrawPoly.java 
	$(JAVAC) $(FLAGS) DrawPoly.java 

# How to run it.
DrawPoly : DrawPoly.class
	$(APPLETVIEWER) DrawPoly.html

# -----------------------------------------------------------------------
# put a label
MyLabel2.class : MyLabel2.java 
	$(JAVAC) $(FLAGS) MyLabel2.java 

# How to run it.
MyLabel2 : MyLabel2.class
	$(APPLETVIEWER) MyLabel2.html

# -----------------------------------------------------------------------
# how about rectangles?
Rectangle2.class : Rectangle2.java 
	$(JAVAC) $(FLAGS) Rectangle2.java 

# How to run it.
Rectangle2 : Rectangle2.class
	$(APPLETVIEWER) Rectangle2.html

# -----------------------------------------------------------------------
# show how to mix a color 
ShowColors.class : ShowColors.java 
	$(JAVAC) $(FLAGS) ShowColors.java 

# How to run it.
ShowColors : ShowColors.class
	$(APPLETVIEWER) ShowColors.html


# -----------------------------------------------------------------------
clean	:  
	rm *.class *.j;
