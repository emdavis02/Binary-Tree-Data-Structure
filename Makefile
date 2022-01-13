# This is the Makefile to use for Homework Assignment #7
# To use, at the prompt, type:
#
#       make		# This will make everything
# or
#       make clean	# This will safely remove old stuff
# or
#	make new	# This will remove all old stuff and
#			# will compile fresh
# or
#	make backup	# To capture a copy of all your .java files

.PHONY: backup clean install new public
.SUFFIXES: .java .class
.java.class:
	javac -g $<

DIR=$(HOME)/../public/hw9/java

all:	Driver Calc

Calc:	Base.java \
	MyLib.java SymTab.java Tree.java Tracker.java \
	Size.java FlushingFileWriter.class \ FlushingPrintWriter.class
	#javac Calculator.java	
	echo 'java Calculator $$*' > Calc
	chmod ug+rx Calc

Driver:	Base.java Driver.java MyLib.java \
	SymTab.java Tree.java Tracker.java Size.java \
	Driver.java FlushingFileWriter.class \ FlushingPrintWriter.class
	javac -g Driver.java
	echo 'java Driver $$*' > Driver
	chmod ug+rx Driver

clean:
	rm -f *.class Driver Calc
	#cp class_files/*.class .
	cp $(DIR)/class_files/*.class .

public:
	rm -f *.class Driver Calc

new:
	make fresh
	make clean
	make

backup:	Makefile *.java
	cp *.java Makefile $(HOME)/hw9/java/backup

fresh:
	rm -f *.datafile
	touch Calc.datafile Driver.datafile
