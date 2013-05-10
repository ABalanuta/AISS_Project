#!/bin/sh 

TARGET_FOLDER=$0

cd `dirname $TARGET_FOLDER`
TARGET_FILE=`basename $TARGET_FOLDER`

# Iterate down a (possible) chain of symlinks
while [ -L "$TARGET_FOLDER" ]
do
    TARGET_FILE=$(readlink $0)
    cd "$(dirname "$TARGET_FOLDER")"
    TARGET_FILE=$(basename "$TARGET_FOLDER")
done

# Compute the canonicalized name by finding the physical path 
# for the directory we're in and appending the target file.
PHYS_DIR=`pwd -P`
RESULT=$PHYS_DIR/$TARGET_FOLDER
BOX=1

java -jar "AESecure.jar" $1 $BOX> "log.txt"
cat log.txt
#rm $SCRIPT"/vars.txt"
