############################# Makefile ##########################
CC = g++
JAVAPATH = /usr/lib/jvm/java-6-openjdk/
CFLAGS = -Wall -fPIC -I ${JAVAPATH}include/ -I ${JAVAPATH}include/linux/ -shared
LDFLAGS =


all: AESboxJNI.o protocol.o
	${CC} ${CFLAGS} AESboxJNI.o protocol.o ${LDFLAGS} -o libaesbox.so

protocol.o: protocol.c protocol.h
	${CC} ${CFLAGS} -c protocol.c

AESboxJNI.o: AESboxJNI.c protocol.h
	${CC} ${CFLAGS} -c AESboxJNI.c

clean:
	rm *.o
	rm *.so
