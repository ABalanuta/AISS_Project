#ifndef UTIL_H
#define UTIL_H


#include <stdio.h>
#include "ethcom.h"
#include <stdlib.h>     /* for exit() */
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h> /* for socket(),... */
#include <netinet/in.h>
#include <arpa/inet.h>
//#include "winsock.h"    /* for socket(),... */
//#include <Windows.h>
//#include "usbcom.h"

int InsPadding(unsigned char barr[EPLENGTH],int n);
int RemPadding(unsigned char barr[EPLENGTH],int n);


#endif
