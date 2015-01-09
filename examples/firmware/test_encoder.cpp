#include <cassert>
#include <csignal>

#include "mraa.hpp"


int running = 1;

void sig_handler(int signo)
{
  if (signo == SIGINT) {
    printf("closing spi nicely\n");
    running = 0;
  }
}

static int aState;
static int bState;
static int count = 0;

int getPhase() {
  // Return a phase from 0 - 3
  aState * 2 + bState;
}

void updateTick(int prevPhase, int curPhase) {
  // Tick forward (possibly wrapping)
  if (curPhase - prevPhase == 1 ||
      curPhase - prevPhase == -3) {
    count++;
  }
  // Tick backward (possibly wrapping)
  else if (curPhase - prevPhase == -1 ||
           curPhase - prevPhase == 3) {
    count--;
  }
  else {
    std::cerr << "Weird phase change: "
              << prevPhase << " to " << curPhase << std::endl;
  }
}

void aHandler(void* args) {
  int prevPhase = getPhase();
  // Get the gpio handle from the args
  mraa::Gpio *encA = (mraa::Gpio*)args;
  aState = encA->read();
  int curPhase = getPhase();
  updateTick(prevPhase, curPhase);
}

void bHandler(void* args) {
  int prevPhase = getPhase();
  // Get the gpio handle from the args
  mraa::Gpio *encB = (mraa::Gpio*)args;
  bState = encB->read();
  int curPhase = getPhase();
  updateTick(prevPhase, curPhase);
}

int main() {
  // Handle Ctrl-C quit
  signal(SIGINT, sig_handler);

  mraa::Gpio *encA = new mraa::Gpio(2);
  assert(encA != nullptr);
  encA->dir(mraa::DIR_IN);
  encA->isr(mraa::EDGE_BOTH, aHandler, encA);
  mraa::Gpio *encB = new mraa::Gpio(3);
  assert(encB != nullptr);
  encB->dir(mraa::DIR_IN);
  encB->isr(mraa::EDGE_BOTH, bHandler, encB);

  while (running) {
    std::cout << "Count(+): " << count << std::endl;
    usleep(100000.0);
  }
}
