#include <stdio.h>
//#include <windows.h>

double PCFreq = 0.0;
	double CounterStart = 0;

void StartCounter()
{
    double li;
    if(!QueryPerformanceFrequency(&li))
      printf("QueryPerformanceFrequency failed!\n");

    PCFreq = ((double)(li.QuadPart))/1000.0;

    QueryPerformanceCounter(&li);
    CounterStart = li.QuadPart;
}

double GetCounter()
{
    double li;
    QueryPerformanceCounter(&li);
    return ((double)li.QuadPart-CounterStart)/PCFreq;
}
