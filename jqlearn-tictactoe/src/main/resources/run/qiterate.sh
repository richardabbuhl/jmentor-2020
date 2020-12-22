#!/bin/bash
echo "Iterations is " $3
java -jar ../target/jqlearn-1.0.0-SNAPSHOT-jar-with-dependencies.jar -o $1 -e $2 -d -n $3

echo -n "1. A="$1, "E="$2, "" >> results.txt
java -jar ../target/jqlearn-1.0.0-SNAPSHOT-jar-with-dependencies.jar -t -d -n 5000 >> results.txt
echo -n "2. A="$1, "E="$2, "" >> results.txt
java -jar ../target/jqlearn-1.0.0-SNAPSHOT-jar-with-dependencies.jar -g -t -d -n 5000 >> results.txt

echo -n "1. A="$1, "E="$2, "" >> results.txt
java -jar ../target/jqlearn-1.0.0-SNAPSHOT-jar-with-dependencies.jar -t -n 10 >> results.txt
echo -n "2. A="$1, "E="$2, "" >> results.txt
java -jar ../target/jqlearn-1.0.0-SNAPSHOT-jar-with-dependencies.jar -g -t -n 10 >> results.txt

##############################
# jqlearn.exe -d -n $1
# jqlearn.exe -t -d -n 2500 >> results.txt
# jqlearn.exe -t -n 1000 >> results.txt
