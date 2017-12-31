# --------------- Pick one of these compilers,  
#JAVAC = javac 
JAVAC = joosc 

# ---------------  Pick one of these flag settings.
#FLAGS = 
FLAGS = -O

# ---------------  Pick one of these java commands.
JAVA = jjava
#JAVA = java

# -----------------------------------------------------------------------
all:	InfoDialog.class AllComponents.class AllEvents.class

# -----------------------------------------------------------------------
# A test of an Information Dialog
InfoDialog.class : InfoDialog.java MultiLineLabel.java \
	IntVector.java StringVector.java
	$(JAVAC) $(FLAGS) InfoDialog.java MultiLineLabel.java \
	IntVector.java StringVector.java

# How to run it.
InfoDialog : InfoDialog.class
	$(JAVA) InfoDialog

# -----------------------------------------------------------------------
# Example of building all the components,  no event handler
AllComponents.class : AllComponents.java BlankCanvas.java \
	              InfoDialog.java IntVector.java Line.java \
		      MultiLineLabel.java ReallyQuitDialog.java \
	              ScrollableScribble.java StringVector.java \
	              YesNoDialog.java
	$(JAVAC) $(FLAGS)  AllComponents.java BlankCanvas.java \
	              InfoDialog.java IntVector.java Line.java \
		      MultiLineLabel.java ReallyQuitDialog.java \
	              ScrollableScribble.java StringVector.java \
	              YesNoDialog.java

# How to run it.
AllComponents : AllComponents.class
	$(JAVA) AllComponents


# -----------------------------------------------------------------------
# Example of building all the components,  with the event handler 
AllEvents.class : AllEvents.java AllComponents.java BlankCanvas.java \
	              InfoDialog.java IntVector.java Line.java \
		      MultiLineLabel.java ReallyQuitDialog.java \
	              ScrollableScribble.java StringVector.java \
	              YesNoDialog.java
	$(JAVAC) $(FLAGS)  AllEvents.java AllComponents.java BlankCanvas.java \
	              InfoDialog.java IntVector.java Line.java \
		      MultiLineLabel.java ReallyQuitDialog.java \
	              ScrollableScribble.java StringVector.java \
	              YesNoDialog.java

# How to run it.
AllEvents : AllEvents.class
	$(JAVA) AllEvents

# -----------------------------------------------------------------------
clean	:  
	rm *.class *.j
