#!/bin/bash          
SCRIPT=$(dirname $(readlink -f $0))
echo $1 > $SCRIPT"/vars.txt"
echo $2 >> $SCRIPT"/vars.txt"
echo $3 >> $SCRIPT"/vars.txt"
echo $4 >> $SCRIPT"/vars.txt"
echo "Java: " >> $SCRIPT"/vars.txt"
#sleep 3
java -jar $SCRIPT"/AESecure.jar" $1 $2 $3 $4 >> $SCRIPT"/vars.txt"
rm $SCRIPT"/vars.txt"
