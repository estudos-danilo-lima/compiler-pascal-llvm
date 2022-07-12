# Modifique as variaveis conforme o seu setup.

JAVA=java
JAVAC=javac

# Eu uso ROOT como o diretório raiz para os meus labs.
ROOT=$(shell pwd)

ANTLR_PATH=$(ROOT)/tools/antlr-4.10.1-complete.jar
PARSER_PATH=$(ROOT)/parser/*

CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)# $(PARSER_PATH)

# Comandos como descritos na página do ANTLR.
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig

# Diretório para aonde vão os arquivos gerados pelo ANTLR.
GEN_PATH=parser

# Diretório aonde está a classe com a função main.
MAIN_PATH=checker

# Diretório para os arquivos .class
BIN_PATH=bin

# Diretório para os casos de teste
DATA=$(ROOT)/tests
IN=$(DATA)/in
OUT=./tests/out

all: antlr javac
	@echo "Done."

antlr: pascal.g4
	$(ANTLR4) -no-listener -visitor -o $(GEN_PATH) pascal.g4

# Compila todos os subdiretórios e joga todos os .class em BIN_PATH pra organizar.
javac:
	rm -rf $(BIN_PATH)
	mkdir $(BIN_PATH)
	$(JAVAC) $(CLASS_PATH_OPTION) -d $(BIN_PATH) */*.java

run:
	$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) $(MAIN_PATH)/Main $(FILE)

runallparser:
	-for FILE in $(DATA)/*.pas; do \
		cd $(GEN_PATH) && \
		$(GRUN) pascal program $${FILE} &&\
		cd .. ; \
	done;

runparser:
	cd $(GEN_PATH) && $(GRUN) pascal program -tokens $(FILE)

clean:
	@rm -rf $(GEN_PATH) $(BIN_PATH) $(OUT)
