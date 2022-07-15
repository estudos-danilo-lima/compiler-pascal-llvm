# Descrição

A partir do lexer e do parser feitos no CP1, iniciamos o processo de avaliação semântica do compilador. Para isso criamos visitadores que englobaram as seguintes operações:
    - Declaração e atribuição de variáveis simples (int, real, str, bool) e compostas (Array).
    - Sistemas de tipos.
    - Estrutura condicional 'if-then-else'.
    - Estrutura de repetição 'repeat'.
    - Declaração e chamadas de função.
    - Funções de entrada (Readln) e saida (Writeln).

A maior dificuldade encontrada até o momento foi o aprendizado da linguagem pascal pois esta tem elementos muito inconsistentes que dificultaram a criação de testes; além disso, os passos iniciais na criação dos visitadores se provaram desafiadores, pois a gramatica estava completa.

Passado o ponto de entender a gramatica e a como criar os visitadores, não tivemos muitos problemas. E por fim, as árvores foram criadas a partir dos visitadores.

Os casos de testes foram alterados a fim de tratar os elementos que nosso compilador reconhece, além da inserção de alguns exemplos com erros pontuais.