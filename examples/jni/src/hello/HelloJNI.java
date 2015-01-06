package hello;

public class HelloJNI {
  public static void main(String[] argv) {
    printHello();
  }

  // Declare the native function
  public static native void printHello();

  static {
    // Assumes this is being run in the root of the project
    System.loadLibrary("hello");
  }
}
