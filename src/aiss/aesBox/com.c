#include "protocol.h"
#include "com.h"
#include <stdlib.h>     /* for exit() */
#include <string.h>
#include <stdio.h>  
#include <sys/types.h>
#include <sys/socket.h> /* for socket(),... */
#include <netinet/in.h>
#include <arpa/inet.h>


static int fd;
static struct sockaddr_in servaddr,cliaddr,SenderAddr;
char servIP[] = {'1','9','2','.','1','6','8','.','0','.','1','4','\0'} ;        /* Server IP address XXX.XXX.XXX.XXX */

int  COMinit()
{
	int n;
	unsigned short Port = 27015;

	// Create a socket for sending data
	fd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if (fd < 0){
		printf("\nSocket Creation FAILED!");
		//close(fd);
		shutdown(fd,2);		
		return -1;
	}

	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(FPGA_PORT);
	servaddr.sin_addr.s_addr = inet_addr(servIP);


	// Bind the socket to any address and the specified port.
	cliaddr.sin_family = AF_INET;
	cliaddr.sin_port = htons(Port);
	cliaddr.sin_addr.s_addr = htonl(INADDR_ANY);

	n = bind(fd, (struct sockaddr *)& cliaddr, sizeof (cliaddr));

	return fd;
}


int sendPacket(u8 *data,u32 size)
{

	return  sendto(fd,(char*)data,size, 0, (struct sockaddr *)& servaddr, sizeof (servaddr));

}
int recvPacket(u8 *buf, u32 size)
{
	int SenderAddrSize = sizeof (SenderAddr);

	int n;

	n = recvfrom(fd,(char*) buf,size, 0, (struct sockaddr *)& SenderAddr, &SenderAddrSize);
	if(n == -1)
		printf("recvfrom error");
	n =  recvfrom(fd,(char*) buf,size, 0, (struct sockaddr *)& SenderAddr, &SenderAddrSize);
	return n;
}
