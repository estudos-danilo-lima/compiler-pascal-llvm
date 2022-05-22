# Modifique as variaveis conforme o seu setup.

JAVA=java
JAVAC=javac

LIB_PATH=library
SRC_PATH=source
EXP_PATH=examples

ANTLR_PATH=$(LIB_PATH)/antlr-4.10.1-complete.jar
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)

ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

all: antlr javac
	@echo "Done."

antlr: lexer.g4 parser.g4
	$(ANTLR4) -o $(SRC_PATH) lexer.g4 parser.g4

javac:
	$(JAVAC) $(CLASS_PATH_OPTION) $(SRC_PATH)/*.java

# Veja a explicação no README
run:
	cd $(SRC_PATH) && $(GRUN) EZLexer tokens -tokens $(FILE)

runall:
	-for FILE in $(EXP_PATH)/*.ezl; do \
	 	cd $(SRC_PATH) && \
	 	echo -e "\nRunning $${FILE}" && \
	 	$(GRUN) EZLexer tokens -tokens $${FILE} && \
	 	cd .. ; \
	done;

clean:
	@rm -rf $(SRC_PATH)
