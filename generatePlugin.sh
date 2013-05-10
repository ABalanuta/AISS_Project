#!/bin/bash
VAR=$1
if [ -z "$1" ]; then 
	echo 
	echo "usage: $0 -box"
	echo "     : for use with HARDWARE AES"
	echo
	echo "usage: $0 -java"
        echo "     : for use with SOFTWARE AES"
	echo
	exit
fi


rm CustomPlugin -R
mkdir CustomPlugin
cp plugin/* -R CustomPlugin/
cd src/aiss/aesBox/
make clean
sh compile.sh
cp libaesbox.* ../../../CustomPlugin/chrome/content/java/
cd ../../../CustomPlugin/
cd chrome/content/java/
if [ "$1"="-box" ]; then
	cp ScriptBoxAES.sh Script.sh
	cp ScriptMACboxAES.sh ScriptMAC.sh
fi

if [ "$1"="-java" ]; then
	cp ScriptJavaAES.sh Script.sh
	cp ScriptMACJavaAES.sh ScriptMAC.sh
fi
cd ../../../
zip -r AESecure.xpi * -x AESecure.xpi -9
rm chrome -R
rm modules -R
rm chrome.manifest install.rdf
clear
echo ###################################
echo #
echo #   Plugin is folder CustomPlugin
echo #
echo ###################################

