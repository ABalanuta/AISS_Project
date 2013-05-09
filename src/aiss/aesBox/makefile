############################# Makefile ##########################
UNAME := ${shell uname}

FILENAME = aiss_aesBox_AESboxJNI

ifeq (${UNAME}, Linux)
CC = g++
JAVAPATH = /usr/lib/jvm/java-6-openjdk/
CFLAGS = -Wall -fPIC -fpermissive -I ${JAVAPATH}include/ -I ${JAVAPATH}include/linux/ -shared
LDFLAGS =

all: ${FILENAME}.o protocol.o
	${CC} ${CFLAGS} ${FILENAME}.o protocol.o ${LDFLAGS} -o libaesbox.so

clean:
	rm *.o
#	rm *.so
endif

ifeq (${UNAME}, Darwin)
CC = g++
JAVAPATH = /Library/Java/JavaVirtualMachines/jdk1.7.0_17.jdk/Contents/Home/include/
CFLAGS = -Wall -fPIC -I ${JAVAPATH} -I ${JAVAPATH}darwin -shared
LDFLAGS = 

all: ${FILENAME}.o protocol.o com.o
	${CC} ${CFLAGS} ${FILENAME}.o protocol.o com.o ${LDFLAGS} -o libaesbox.jnilib

clean:
	rm *.o
	rm *.jnilib
endif

main: main.o com.o protocol.o
	${CC} -Wall -fpermissive -g main.o protocol.o com.o -o main

main.o: main.c 
	${CC} -Wall -fpermissive -g -c main.c -o main.o

protocol.o: protocol.c protocol.h 
	${CC} ${CFLAGS} -c protocol.c -o protocol.o

${FILENAME}.o: protocol.o com.o ${FILENAME}.c 
	${CC} ${CFLAGS} -c ${FILENAME}.c

#counter.o: counter.c counter.h
#	${CC} -Wall -fpermissive -g -c counter.c -o counter.o

com.o: com.c com.h
	${CC} -Wall -fpermissive -c com.c -o com.o

