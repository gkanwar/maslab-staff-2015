// Build with:
// gcc test_gpio.c -o test_gpio -lmraa

#include <stdio.h>
#include <unistd.h>

#include "mraa.h"

int main() {
  mraa_init();

  mraa_gpio_context gpio;
  gpio = mraa_gpio_init(13);
  if (gpio == NULL) {
    printf("Gpio null!\n");
    exit(1);
  }
  mraa_result_t r = mraa_gpio_dir(gpio, MRAA_GPIO_OUT);
  if (r != MRAA_SUCCESS) {
    mraa_result_print(r);
    exit(2);
  }

  while (1) {
    printf("Gpio high\n");
    r = mraa_gpio_write(gpio, 1);
    if (r != MRAA_SUCCESS) {
      mraa_result_print(r);
      exit(3);
    }
    sleep(1);
    printf("Gpio low\n");
    r = mraa_gpio_write(gpio, 0);
    if (r != MRAA_SUCCESS) {
      mraa_result_print(r);
      exit(4);
    }
    sleep(1);
  }

  mraa_gpio_close(gpio);
}
