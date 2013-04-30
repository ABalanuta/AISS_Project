############################# Makefile ##########################
UNAME := ${shell uname}

ifeq (${UNAME}, Linux)
CC = g++
JAVAPATH = /usr/lib/jvm/java-6-openjdk/
CFLAGS = -Wall -fPIC -I ${JAVAPATH}include/ -I ${JAVAPATH}include/linux/ -shared
LDFLAGS =

all: AESboxJNI.o protocol.o
	${CC} ${CFLAGS} AESboxJNI.o protocol.o ${LDFLAGS} -o libaesbox.so

clean:
	rm *.o
	rm *.so
endif

ifeq (${UNAME}, Darwin)
CC = g++
JAVAPATH = /Library/Java/JavaVirtualMachines/jdk1.7.0_17.jdk/Contents/Home/include/
CFLAGS = -Wall -fPIC -I ${JAVAPATH} -I ${JAVAPATH}/darwin -shared
LDFLAGS =

all: AESboxJNI.o protocol.o
	${CC} ${CFLAGS} AESboxJNI.o protocol.o ${LDFLAGS} -o libaesbox.jnilib

clean:
	rm *.o
	rm *.jnilib
endif

protocol.o: protocol.c protocol.h
	${CC} ${CFLAGS} -c protocol.c

AESboxJNI.o: AESboxJNI.c protocol.h
	${CC} ${CFLAGS} -c AESboxJNI.c
