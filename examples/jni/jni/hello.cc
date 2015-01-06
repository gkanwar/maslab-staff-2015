#include <jni.h>
#include <iostream>

// Include is the fully qualified class name separated by underscores
#include "hello_HelloJNI.h"

JNIEXPORT void JNICALL Java_hello_HelloJNI_printHello(JNIEnv *env, jclass thisCls) {
  std::cout << "Hello, world!" << std::endl;
  return;
}
