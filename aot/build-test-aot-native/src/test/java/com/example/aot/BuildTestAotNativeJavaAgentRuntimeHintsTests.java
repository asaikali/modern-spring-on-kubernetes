package com.example.aot;

import com.example.aot.runtimehints.AotAgent;
import org.junit.jupiter.api.Test;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.test.agent.EnabledIfRuntimeHintsAgent;
import org.springframework.aot.test.agent.RuntimeHintsInvocations;
import org.springframework.aot.test.agent.RuntimeHintsRecorder;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfRuntimeHintsAgent
public class BuildTestAotNativeJavaAgentRuntimeHintsTests {
    @Test
    void shouldRegisterReflectionHints() {
        System.out.println("Testing Runtime Hints... ");
        RuntimeHints runtimeHints = new RuntimeHints();

        // this pattern, if registered, will contribute the ResourceHint to the image
        runtimeHints.resources().registerPattern("app-resources.properties");

        RuntimeHintsInvocations invocations = RuntimeHintsRecorder.record(() -> {
            AotAgent helloService = new AotAgent(new ClassPathResource("app-resources.properties"));
            helloService.sayHello("Native");
        });
        assertThat(invocations).match(runtimeHints);
    }
}
