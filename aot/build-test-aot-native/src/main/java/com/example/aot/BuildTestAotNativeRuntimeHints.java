package com.example.aot;

import com.example.aot.runtimehints.HelloServiceImpl;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

public class BuildTestAotNativeRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // register reflection hints
        hints.reflection()
                .registerConstructor(
                        HelloServiceImpl.class.getConstructors()[0], ExecutableMode.INVOKE)
                .registerMethod(Objects.requireNonNull(ReflectionUtils.findMethod(
                        HelloServiceImpl.class, "sayHello", String.class)), ExecutableMode.INVOKE);

        // register resource hints
        hints.resources().registerPattern("hello.txt");

        // register serialization hints
        hints.serialization().registerType(java.util.ArrayList.class);
        hints.serialization().registerType(java.lang.Long.class);
        hints.serialization().registerType(java.lang.Number.class);

        // register dynamic proxies
        hints.proxies().registerJdkProxy(java.util.Map.class);
        hints.reflection().registerType(java.util.Map.class);
    }
}
