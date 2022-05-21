JAVA=java
JAVAC=javac

LIB_PATH=library
SRC_PATH=source

ANTLR_PATH=$(LIB_PATH)/antlr-4.10.1-complete.jar
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)

# baseado no exemplo do professor
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

#configurar corretamente depois


all: antlr javac
	@echo "Done."

#antlr: source/lexer.g4 source/parser.g4
#	$(ANTLR4)
antlr: 
	$(ANTLR4) $(GNAME).g

javac:
	$(JAVAC) $(CLASS_PATH_OPTION) *.java

run:
	$(GRUN) $(GNAME) tokens $(FILE)

clean:
	@rm -rf $(GNAME).class $(GNAME).interp $(GNAME).java $(GNAME).tokens
