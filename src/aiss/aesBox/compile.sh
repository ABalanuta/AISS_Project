cd ../../.
javac aiss/aesBox/AESboxJNI.java
javah -d aiss/aesBox/ aiss.aesBox.AESboxJNI
cd aiss/aesBox/
make clean
make
echo "###########################################"
echo "# O file libaesbox.os fica ao lado do Jar #"
echo "###########################################"
