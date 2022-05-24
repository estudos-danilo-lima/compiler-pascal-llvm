## Compiler for pascal to llvm
Compiler for pascal language to llvm

# Requirements
- antlr
- java and javac

# Installation
To install this compiler you must clone this repository as:
  ``` 
  git clone https://github.com/daniloelima/compiler-pascal-llvm.git 
  ```

# Running
To run you must first generate the lexer and parser, like:
  ``` 
   make 
  ```
Then, you can either run a single example like:
  ``` 
   make run FILE=../tests/in/SELECT AN EXAMPLE FROM THE AVAILABLE ONES.
  ```

Or run all the available examples like:
  ```
  make runall
  ```

Alternatively, runing the snipet bellow will read input from stdin, when finished press 'CTRL+D':
  ```
    make run
  ```