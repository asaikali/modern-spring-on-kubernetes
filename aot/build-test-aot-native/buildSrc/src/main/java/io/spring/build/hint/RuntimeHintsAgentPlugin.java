/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.build.hint;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.testing.Test;

/**
 * {@link Plugin} that configures the {@code RuntimeHints} Java agent to test tasks.
 *
 * @author Brian Clozel
 */
public class RuntimeHintsAgentPlugin implements Plugin<Project> {

	public static final String RUNTIMEHINTS_TEST_TASK = "runtimeHintsTest";
	private static final String EXTENSION_NAME = "runtimeHintsAgent";


	@Override
	public void apply(Project project) {
		project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
			Dependency springCoreTest = project.getDependencies().create("org.springframework:spring-core-test:6.0.0-SNAPSHOT@jar");
			Configuration agentConfiguration = project.getRootProject().getConfigurations().detachedConfiguration(springCoreTest);
			RuntimeHintsAgentExtension agentExtension = project.getExtensions().create(EXTENSION_NAME,
					RuntimeHintsAgentExtension.class, project.getObjects());
			Test agentTest = project.getTasks().create(RUNTIMEHINTS_TEST_TASK, Test.class, test -> {
				test.useJUnitPlatform(options -> {
					options.includeTags("RuntimeHintsTests");
				});
				test.include("**/*Tests.class", "**/*Test.class");
				test.systemProperty("java.awt.headless", "true");
			});
			project.afterEvaluate(p -> {
				File agentJarFile = agentConfiguration.getSingleFile();
				agentTest.jvmArgs("-javaagent:" + agentJarFile + "=" + agentExtension.asJavaAgentArgument());
			});
			project.getTasks().getByName("check", task -> task.dependsOn(agentTest));
		});
	}
}
