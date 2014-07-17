./build.sh $1
./tinyR testcases/$1.out | head -n7
./tinyR outs/$1.myout | head -n7
