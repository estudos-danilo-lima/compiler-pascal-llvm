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

# Diretório para os arquivos .class
BIN_PATH=bin

# Diretório para os casos de teste
DATA=$(ROOT)/tests
IN=$(DATA)/in/
OUT=./tests/out/

OUT_LL=$(OUT)$(basename $(notdir $(FILE))).ll

all: antlr javac
	@echo "Done."

antlr: pascal.g4
	$(ANTLR4) -no-listener -visitor -o $(GEN_PATH) pascal.g4

# Compila todos os subdiretórios e joga todos os .class em BIN_PATH pra organizar.
javac:
	rm -rf $(BIN_PATH)
	mkdir $(BIN_PATH)
	$(JAVAC) $(CLASS_PATH_OPTION) -d $(BIN_PATH) */*.java Main.java

run:
	$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $(FILE)

# This generates the targer <file>.ll
$(OUT_LL) ll:
	@mkdir -p tests/out -p
	@$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $(FILE) > $(OUT_LL)

# This runs the .ll file generated from the input pascal program
lli: $(OUT_LL)
	-@lli $(OUT_LL) || true

runall:
	@-for FILE in $(IN)*.pas; do \
	 	echo "Running $${FILE}" && \
	 	make lli OUT=$(DATA)/out/ FILE=$${FILE} -s &&\
		echo;\
	done;

clean:
	@rm -rf $(GEN_PATH) $(BIN_PATH) $(OUT)
