#!/bin/bash
rm results.txt
# for ((i = 5000; i <= 25000; i += 1000)) ; do
for ((i = 5000; i <= 50000; i += 1000)) ; do
  echo $i " games" >> results.txt
  echo $i " games"
  ./qiterate.sh 0.10 0.00 $i
  ./qiterate.sh 0.10 0.05 $i
  ./qiterate.sh 0.10 0.10 $i
  ./qiterate.sh 0.10 0.15 $i
  ./qiterate.sh 0.10 0.20 $i
  ./qiterate.sh 0.10 0.25 $i

  ./qiterate.sh 0.05 0.10 $i
  ./qiterate.sh 0.10 0.10 $i
  ./qiterate.sh 0.15 0.10 $i
  ./qiterate.sh 0.20 0.10 $i
  ./qiterate.sh 0.25 0.10 $i
done
