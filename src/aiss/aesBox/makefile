############################# Makefile ##########################
UNAME := ${shell uname}

FILENAME = aiss_aesBox_AESboxJNI

ifeq (${UNAME}, Linux)
CC = g++
JAVAPATH = /usr/lib/jvm/java-6-openjdk/
CFLAGS = -Wall -fPIC -fpermissive -I ${JAVAPATH}include/ -I ${JAVAPATH}include/linux/ -shared
LDFLAGS =

all: ${FILENAME}.o protocol.o com.o
	${CC} ${CFLAGS} ${FILENAME}.o protocol.o com.o ${LDFLAGS} -o libaesbox.so
clean:
	rm *.o
#	rm *.so
endif

ifeq (${UNAME}, Darwin)
CC = g++
JAVAPATH = /Library/Java/JavaVirtualMachines/jdk1.7.0_17.jdk/Contents/Home/include/
CFLAGS = -Wall -fPIC -fpermissive -I ${JAVAPATH} -I ${JAVAPATH}darwin -shared
LDFLAGS = 

all: ${FILENAME}.o protocol.o com.o
	${CC} ${CFLAGS} ${FILENAME}.o protocol.o com.o ${LDFLAGS} -o libaesbox.jnilib

clean:
	rm *.o
	rm *.jnilib
endif

${FILENAME}.o: ${FILENAME}.c ${FILENAME}.h protocol.o com.o
	${CC} ${CFLAGS} -c ${FILENAME}.c -o ${FILENAME}.o

main: main.o com.o protocol.o
	${CC} -Wall -fpermissive -g main.o protocol.o com.o -o main

main.o: main.c com.o protocol.o
	${CC} -Wall -fpermissive -c main.c

protocol.o: protocol.c protocol.h
	${CC} ${CFLAGS} -c protocol.c -o protocol.o

com.o: com.c com.h protocol.h
	${CC} ${CFLAGS} -c com.c -o com.o
