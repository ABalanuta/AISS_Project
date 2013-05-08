############################# Makefile ##########################
FILENAME = aiss_aesBox_AESboxJNI
CC = g++
JAVAPATH = /usr/lib/jvm/java-6-openjdk/
CFLAGS = -Wall -fpermissive -fPIC -I ${JAVAPATH}include/ -I ${JAVAPATH}include/linux/ -shared
LDFLAGS = 
all: ${FILENAME}.o protocol.o com.o
	${CC} ${CFLAGS} ${FILENAME}.o protocol.o com.o ${LDFLAGS} -o libaesbox.so

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

clean:
	rm *.o
	rm *.so
