# Modifique as variaveis conforme o seu setup.

JAVA=java
JAVAC=javac

# Eu uso ROOT como o diretório raiz para os meus labs.
# YEAR=$(shell pwd | grep -o '20..-.')
# ROOT=/home/zambon/Teaching/$(YEAR)/CC/labs

ROOT=/home/fernando/Documentos/compiladores/compiler-pascal-llvm

ANTLR_PATH=$(ROOT)/tools/antlr-4.10.1-complete.jar
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)

# Comandos como descritos na página do ANTLR.
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

# Diretório para aonde vão os arquivos gerados.
GEN_PATH=lexer

# Diretório para os casos de teste
DATA=$(ROOT)/tests
IN=$(DATA)/in

all: antlr javac
	@echo "Done."

antlr: pascal.g4
	$(ANTLR4) -o $(GEN_PATH) pascal.g4

javac:
	$(JAVAC) $(CLASS_PATH_OPTION) $(GEN_PATH)/*.java

# Veja a explicação no README
run:
	cd $(GEN_PATH) && $(GRUN) pascal program $(FILE)

runall:
	-for FILE in $(IN)/*.pas; do \
	 	cd $(GEN_PATH) && \
	 	echo -e "\nRunning $${FILE}" && \
	 	$(GRUN) pascal program $${FILE} && \
	 	cd .. ; \
	done;

clean:
	@rm -rf $(GEN_PATH)