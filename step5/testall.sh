#! /bin/bash

make clean; 
make;
FilesList=$(ls -l testcases/*.micro | cut -d' ' -f9) #get paths of all testcases
IFS=' '
FilesList=($FilesList) #store testcases into array
FilesList=$(echo $FilesList | cut -d'/' -f2 | cut -d'.' -f1) #get filename out of path
echo $FilesList > temptestcasesfile
while read Testcase
do
	echo ">>>TESTCASE: $Testcase.micro"
	java -cp lib/antlr.jar:classes Micro testcases/$Testcase.micro > outs/$Testcase.myout
	if [[ $# -ne 0 ]]
	then
		Mine=$(./tinyR outs/$Testcase.myout | head -n1)
		Expected=$(./tinyR testcases/$Testcase.out | head -n1)
		echo "Expected result: $Expected"
		echo "Achieved result: $Mine"
		echo
	else
		diff outs/$Testcase.myout testcases/$Testcase.out
		echo
	fi
	
done < temptestcasesfile
#rm temptestcasesfile
