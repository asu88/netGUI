
LIBRARIES=../libs/piccolo.jar:../libs/piccolox.jar
ifeq ($(JAVA_HOME),"")
JAVAC=$(JAVA_HOME)/bin/javac -encoding latin1
else
JAVAC=javac -Xlint -encoding latin1
endif


install: all
	cp *.class ../classes/

all:
	$(JAVAC) -classpath $(LIBRARIES) *.java

%.class: %.java
	$(JAVAC) -classpath $(LIBRARIES) $<

clean:
	-rm -f *.class
