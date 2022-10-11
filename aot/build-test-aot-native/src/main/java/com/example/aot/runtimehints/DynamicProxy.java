package com.example.aot.runtimehints;

import java.lang.reflect.Proxy;
import java.util.Map;

public class DynamicProxy implements HelloService {
    @Override
    public String sayHello(String name) {
        // A proxy instance serviced by an invocation handler is created via a factory method call on the java.lang.reflect.Proxy class
        // Since InvocationHandler is a functional interface, let's the handler inline using lambda expressions
        Map proxyInstance;
        try {
            proxyInstance = (Map) Proxy.newProxyInstance(
                    DynamicProxy.class.getClassLoader(),
                    new Class[] {
                            Class.forName(name)
                    },
                    (proxy, method, methodArgs) -> {
                        if (method.getName().equals("get")) {
                            return 101;
                        }  else {
                            throw new UnsupportedOperationException(
                                    "Unsupported method: " + method.getName());
                        }
                    });
        } catch (ClassNotFoundException e) {
            return "Class no be proxied not found: " + name;
        }

        // successful invocation of a get() operation
        int oneoone = (int) proxyInstance.get("hello"); // 101
        System.out.println("Invoking method: <get> in a <java.util.Map>. Output: " + oneoone);

        // failed proxy invocation, as put() is not a supported operation in the proxy
        try {
            System.out.println("Invoking method: <put> in a <java.util.Map>");
            proxyInstance.put("hello", "world"); // exception
        } catch (Exception e) {
            System.out.println("DynamicProxy: Expected an exception, got an exception: put is not supported in the proxy.");
        }
        // some unexpected exception
        return "DynamicProxy invocation: " + name;
    }
}
