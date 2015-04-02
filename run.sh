#!/bin/bash

if [ $# -lt 1 ]; then
  echo "$0 file.lcpl [build_comp]"
  exit
fi

if [ $# -gt 1 ]; then

  cd parser
  make build
  cd ../semantic
  make build
  cd ../code
  make build
  cd ..
fi

java -cp "parser/lib/*:parser/bin" LCPLParser $1 $1.ast
java -cp "semantic/bin:semantic/lib/*" LCPLSemant $1.ast $1.run
java -cp "code/bin:code/lib/*" LCPLCodeGen $1.run $1.ir

llc $1.ir; clang $1.ir.s lcpl_runtime.c -o $1.exe

