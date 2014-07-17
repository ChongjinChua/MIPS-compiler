#! /bin/bash

make clean; 
make;
if [[ $# -eq 0 ]]
then
	java -cp lib/antlr.jar:classes Micro testcases/step4_testcase.micro
else
	java -cp lib/antlr.jar:classes Micro testcases/$1.micro > outs/$1.myout
	diff -y outs/$1.myout testcases/$1.out
fi
