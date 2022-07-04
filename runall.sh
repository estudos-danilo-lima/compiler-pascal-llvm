#!/bin/bash

YEAR=$(pwd | grep -o '20..-.')
# Ajeite essa variável como feito no Makefile
ROOT=/home/miguel/compiladores/compiler-pascal-llvm
DATA=$ROOT/tests
IN=$DATA/in
OUT=./out05

ANTLR_PATH=$ROOT/tools/antlr-4.10.1-complete.jar
CLASS_PATH_OPTION="-cp .:$ANTLR_PATH"

rm -rf $OUT
mkdir $OUT
for infile in `ls $IN/*.pas`; do
    base=$(basename $infile)
    outfile=$OUT/${base/.pas/.out}
    dotfile=$OUT/${base/.pas/.dot}
    pdffile=$OUT/${base/.pas/.pdf}
    echo Running $base
    java $CLASS_PATH_OPTION:bin checker/Main $infile 1> $outfile 2> $dotfile
    dot -Tpdf $dotfile -o $pdffile
done
