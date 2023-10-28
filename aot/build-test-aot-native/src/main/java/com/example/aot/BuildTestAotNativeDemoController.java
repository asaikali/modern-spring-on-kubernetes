package com.example.aot;

import com.example.aot.runtimehints.*;
import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ImportRuntimeHints(BuildTestAotNativeRuntimeHints.class)
public class BuildTestAotNativeDemoController {
  private final ObjectProvider<HelloService> helloServices;

  BuildTestAotNativeDemoController(ObjectProvider<HelloService> helloServices) {
    this.helloServices = helloServices;
  }

  @GetMapping("/hello")
  HelloResponse hello(@RequestParam(required = false) String mode) throws Exception {
    String message = getHelloMessage(mode, "Native");
    return new HelloResponse(message);
  }

  private String getHelloMessage(String mode, String name) throws Exception {
    if (Optional.ofNullable(mode).isEmpty()) return "No option provided";

    switch (mode) {
      case "bean" -> {
        HelloService service = this.helloServices.getIfUnique();
        return (service != null) ? service.sayHello(name) : "No bean found";
      }
      case "reflection" -> {
        String implementationName =
            Optional.ofNullable(HelloServiceImpl.getDefaultHelloServiceImplementation())
                .orElse(HelloServiceImpl.class.getName());
        Class<?> implementationClass =
            ClassUtils.forName(implementationName, getClass().getClassLoader());
        Method method = implementationClass.getMethod("sayHello", String.class);
        Object instance = BeanUtils.instantiateClass(implementationClass);
        return (String) ReflectionUtils.invokeMethod(method, instance, name);
      }
      case "resource" -> {
        Resource helloService = new Resource(new ClassPathResource("hello.txt"));
        return helloService.sayHello(name);
      }
      case "agent" -> {
        AotAgent helloService = new AotAgent(new ClassPathResource("app-resources.properties"));
        return helloService.sayHello(name);
      }
      case "serialization" -> {
        Serialization service = new Serialization();
        return service.sayHello(name);
      }
      case "proxy" -> {
        DynamicProxy service = new DynamicProxy();
        return service.sayHello("java.util.Map");
      }
      default -> {
        return "Unknown mode: " + mode;
      }
    }
  }

  //	 Response from the service
  public record HelloResponse(String message) {}
}
