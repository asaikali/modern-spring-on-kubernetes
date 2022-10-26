package com.example.aot.runtimehints;

public class HelloServiceImpl implements HelloService {
  @Override
  public String sayHello(String name) {
    return sayHello("Hello", name);
  }

  // Tricking GraalVM to not deduce a constant
  public static String getDefaultHelloServiceImplementation() {
    return null;
  }
}
