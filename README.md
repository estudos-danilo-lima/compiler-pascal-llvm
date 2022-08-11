# Compiler for pascal to llvm
Compiler for pascal language to llvm

## Reports
All the reports about the checkpoints are inside the documentation folder.

## Output
After running the code, the out folder will be available inside the tests folder

## Requirements
- java and javac

## Installation
To install this compiler you must clone this repository as:
  ``` 
  git clone https://github.com/daniloelima/compiler-pascal-llvm.git 
  ```

  ```
  sudo apt install llvm-runtime
  ```

## Running
After clonning the repository, you need to acess the folder, like:
  ```
  cd compiler-pascal-llvm
  ```

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
  ./runall.sh
  ```

Alternatively, runing the snipet bellow will read input from stdin, when finished press 'CTRL+D':
  ```
  make run
  ```