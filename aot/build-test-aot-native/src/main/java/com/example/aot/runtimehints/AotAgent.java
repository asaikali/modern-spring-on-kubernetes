package com.example.aot.runtimehints;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AotAgent implements HelloService {

    private final org.springframework.core.io.Resource resource;

    public AotAgent(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String sayHello(String name) {
        Properties prop = new Properties();

        // read properties
        try (InputStream inputStream = this.resource.getInputStream()) {
            prop.load(inputStream);
            System.out.println("Reading config.threads property: " + prop.getProperty("config.threads"));
            System.out.println("Reading config.load property: " + prop.getProperty("config.load"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sayHello("AotAgent for resource: ", name);
    }
}
