package com.example;

import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.resource.LoadableResource;
import org.flywaydb.core.internal.resource.classpath.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class GraalVMResourceProvider implements ResourceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraalVMResourceProvider.class);

    private final Location[] locations;

    public GraalVMResourceProvider(Location[] locations) {
        this.locations = locations;
    }

    @Override
    public LoadableResource getResource(String name) {
        LOGGER.debug("getResource('{}')", name);
        if (getClassLoader().getResource(name) == null) {
            return null;
        }
        return new ClassPathResource(null, name, getClassLoader(), StandardCharsets.UTF_8);
    }

    @Override
    public Collection<LoadableResource> getResources(String prefix, String[] suffixes) {
        LOGGER.debug("getResources('{}', {})", prefix, Arrays.toString(suffixes));

        try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create("resource:/"), Map.of())) {
            List<LoadableResource> result = new ArrayList<>();
            for (Location location : locations) {
                Path path = fileSystem.getPath(location.getPath());
                try (Stream<Path> files = Files.walk(path)) {
                    files
                            .filter(Files::isRegularFile)
                            .filter(file -> file.getFileName().toString().startsWith(prefix))
                            .filter(file -> hasSuffix(file.getFileName().toString(), suffixes))
                            .map(file -> (LoadableResource) new ClassPathResource(null, file.toString(), getClassLoader(), StandardCharsets.UTF_8))
                            .forEach(result::add);
                }
            }
            return result;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static boolean hasSuffix(String input, String[] suffixes) {
        for (String suffix : suffixes) {
            if (input.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private static ClassLoader getClassLoader() {
        return GraalVMResourceProvider.class.getClassLoader();
    }
}
