#! /bin/bash

make clean; make; java -cp lib/antlr.jar:classes Micro testcases/input/test5.micro
