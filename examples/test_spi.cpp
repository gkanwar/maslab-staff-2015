// Build with:
// g++ test_spi.cpp -o test_spi -lmraa

#include <unistd.h>
#include <stdint.h>
#include <signal.h>
#include <sys/time.h>

#include "mraa.hpp"

int running = 0;

void sig_handler(int signo)
{
  if (signo == SIGINT) {
    printf("closing spi nicely\n");
    running = -1;
  }
}

int main()
{
  signal(SIGINT, sig_handler);
  mraa::Spi* spi = new mraa::Spi(0);
  spi->bitPerWord(32);
  char rxBuf[2];
  char writeBuf[4];
  unsigned int sensorRead = 0x20000000;
  writeBuf[0] = sensorRead & 0xff;
  writeBuf[1] = (sensorRead >> 8) & 0xff;
  writeBuf[2] = (sensorRead >> 16) & 0xff;
  writeBuf[3] = (sensorRead >> 24) & 0xff;
  float total = 0;
  struct timeval tv;
  int init = 0;
  while (running == 0) {
    char* recv = spi->write(writeBuf, 4);
    if (recv) {
      unsigned int recvVal = 0 | recv[3];
      recvVal = (recvVal << 8) | recv[2];
      recvVal = (recvVal << 8) | recv[1];
      recvVal = (recvVal << 8) | recv[0];
      //printf("Received: 0x%.8x\n", recvVal);
      // Sensor reading
      short reading = (recvVal >> 10) & 0xffff;
      if (init) {
	unsigned long long ms = (unsigned long long)(tv.tv_sec)*1000 +
		(unsigned long long)(tv.tv_usec) / 1000;
	gettimeofday(&tv, NULL);
	ms -= (unsigned long long)(tv.tv_sec)*1000 +
		(unsigned long long)(tv.tv_usec) / 1000;
	int msi = (int)ms;
	float msf = (float)msi;
	float rf = (float)reading;
        total += -0.001 * msf * (rf / 80.0);
        printf("Reading: %f, Total: %f, Time: %f\n", rf, total, -msf);
      }
      else {
	init = 1;
	gettimeofday(&tv, NULL);
      }
    }
    else {
      printf("No recv\n");
    }
    usleep(5000);
  }

  delete spi;

  return 0;
}
