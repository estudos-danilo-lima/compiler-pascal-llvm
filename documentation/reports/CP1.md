# Descrição

O grupo escolheu utilizar a ferramenta antlr para realizar a análise léxica e sintática da linguagem pascal. Para tal, foi usada a gramática contida no seguinte repositório:

https://github.com/antlr/grammars-v4/tree/master/pascal

O lexer e parser disponibilizado pela ferramenta esta completo (em relação a ISO 7185:1990) e até o momento da realização do CP1 foi decidido não realizar alterações na gramática.

Resolvemos deixar a instalação do compilador o mais auto contida quanto possivel, criando uma pasta que contem o jar do antlr pronto para uso, além de uma pasta que contém uma diversidade de testes (tanto normais quanto com erros).

A maior dificuldade encontrada até o momento foi o aprendizado da linguagem pascal e a adaptação do Makefile disponibilizado pelo professor em outros laboratórios para o trabalho.