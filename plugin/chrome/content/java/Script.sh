#!/bin/bash          
SCRIPT=$(dirname $(readlink -f $0))
cd $SCRIPT
java -jar '-Djava.library.path=/usr/local/lib/pteid_jni/' "AESecure.jar" $1  > "log.txt"
rm input.zip
cat log.txt
