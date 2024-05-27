package com.instrapp.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;

@RestController
public class ConfigController {

    @RequestMapping("/configs/**")
    public Resource getConfigFile(HttpServletRequest request) throws IOException {
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        fullPath = Objects.requireNonNull(fullPath).replaceFirst("/configs/", "");

        Path configDir = Paths.get("/tmp");

        // Find the latest config-repo-* directory
        Path latestConfigRepo = Files.list(configDir)
                .filter(path -> Files.isDirectory(path) && path.getFileName().toString().startsWith("config-repo-"))
                .max(Comparator.comparing(Path::getFileName))
                .orElseThrow(() -> new RuntimeException("No config repository found"));

        // Construct the full path to the requested file
        Path configFile = latestConfigRepo.resolve(fullPath);

        if (!Files.exists(configFile) || !Files.isRegularFile(configFile)) {
            throw new RuntimeException("Config file not found: " + configFile);
        }

        return new FileSystemResource(configFile.toFile());
    }
}
