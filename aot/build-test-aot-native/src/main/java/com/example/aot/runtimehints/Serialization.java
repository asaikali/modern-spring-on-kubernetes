package com.example.aot.runtimehints;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Serialization implements HelloService{
    private static final String filename = "serialized_objects_in_stream";

    static Stream<Long> fibonacciStream() {
        return Stream.iterate(new long[]{0, 1}, (f) -> new long[]{f[0] + f[1], f[0]}).map(f -> f[0]);
    }

    @Override
    public String sayHello(String name) {
        List<Long> fib10 = fibonacciStream().limit(10).collect(Collectors.toList());
        try (ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream(filename))) {
            oss.writeObject(fib10);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "Serialization exception: " + name;
        }

        Object deserializedFib1000;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            deserializedFib1000 = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return "Could not open input stream to read object";
        }

        System.out.println("Serialized list matches Deserialized list: " + fib10.equals(deserializedFib1000));
//        System.out.println("Print the first 10 Fibonacci numbers in the sequence");
//        fib10.forEach(System.out::println);

        return String.format("Serialization: %s Fibonacci generation", name);
    }



}
