package com.example.aot;

import com.example.aot.runtimehints.HelloService;
import com.example.aot.runtimehints.HelloServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class BuildTestAotNativeConfiguration {
	@Bean
	HelloService helloService() {
		return new HelloServiceImpl();
	}
}
