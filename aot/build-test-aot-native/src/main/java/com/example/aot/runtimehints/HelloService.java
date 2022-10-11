package com.example.aot.runtimehints;

public interface HelloService {

	String sayHello(String name);

	default String sayHello(String prefix, String name) {
		return String.format("%s %s", prefix, name);
	}

}
