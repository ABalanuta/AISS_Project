#!/bin/bash          
SCRIPT=$(dirname $(readlink -f $0))
echo $1
java -cp $SCRIPT Test 
