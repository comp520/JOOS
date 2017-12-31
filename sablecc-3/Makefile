########################################################
# Makefile for JJOOS
#
# $Id: Makefile,v 1.3 2003/07/29 17:16:00 fqian Exp $

JAVAC=javac -d classes -classpath classes:src:$$CLASSPATH
SABLECC=sablecc

SOURCES=${shell find src -name "*.java" -print}
TARGETS=$(subst src,classes,$(SOURCES:.java=.class))

########################################################
# compile all the sources
#
all: grammar ${TARGETS}

$(TARGETS): classes/%.class: src/%.java
	$(JAVAC) $<

grammar: src/joos.sablecc
	$(SABLECC) -d src src/joos.sablecc
	mkdir -p classes/joosc/lexer/ classes/joosc/parser/
	cp src/joosc/lexer/lexer.dat classes/joosc/lexer/
	cp src/joosc/parser/parser.dat classes/joosc/parser/

clean:
	find classes -name '*.class' | xargs rm
	find classes -name '*.dat' | xargs rm
