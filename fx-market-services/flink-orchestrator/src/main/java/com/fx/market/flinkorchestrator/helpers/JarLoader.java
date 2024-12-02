package com.fx.market.flinkorchestrator.helpers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class JarLoader {

    @SneakyThrows
    public List<File> loadFlinkProcessorWithResourceLoader() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:flink-jobs/*");
        Arrays.stream(resources).toList().forEach(
                flinkJar -> log.info("Loaded file: {}", flinkJar.getFilename())
        );
        return Arrays.stream(resources)
                .map(resource -> {
                    try {
                        return resource.getFile();
                    } catch (IOException iioException) {
                        log.error("TODO - I'm PoC - Fix me...");
                    }
                    return null;
                }).toList();
    }

}
