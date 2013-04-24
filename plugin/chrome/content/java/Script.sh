#!/bin/bash          
SCRIPT=$(dirname $(readlink -f $0))
#echo $1 > $SCRIPT"/vars.txt"
#echo "Java: " >> $SCRIPT"/vars.txt"
#sleep 3
#java -jar $SCRIPT"/AESecure.jar" $1 >> $SCRIPT"/vars.txt"
java -jar $SCRIPT"/AESecure.jar" $1
#rm $SCRIPT"/vars.txt"
