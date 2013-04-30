gcc -o protocol -c protocol.c -Wall
g++ -fPIC -I "/usr/lib/jvm/java-8-oracle/include/" -I "/usr/lib/jvm/java-8-oracle/include/linux/" -shared -o libaesbox.so protocol.o AESboxJNI.c -Wall
