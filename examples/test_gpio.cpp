// Build with:
// gcc test_gpio.c -o test_gpio -lmraa

#include <csignal>
#include <iostream>
#include <unistd.h>

#include "mraa.hpp"

int running = 1;

void sig_handler(int signo)
{
  if (signo == SIGINT) {
    printf("closing spi nicely\n");
    running = 0;
  }
}

int main() {
  // Handle Ctrl-C quit
  signal(SIGINT, sig_handler);

  // LED is connected to pin 13
  mraa::Gpio gpio = mraa::Gpio(13);
  gpio.dir(mraa::DIR_OUT);

  while (running) {
    printf("Gpio high\n");
    gpio.write(1);
    sleep(1);
    printf("Gpio low\n");
    gpio.write(0);
    sleep(1);
  }
}
