package com.instrapp.config;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class GitService {

    @Value("${github.repo.url}")
    private String REPO_URL;

    @Value("${github.repo.username}")
    private String GIT_USERNAME;

    @Value("${github.repo.password}")
    private String GIT_PASSWORD;



    @Scheduled(fixedRate = 300000) // Schedule this task to run every 5 minute
    public void cloneRepo() throws GitAPIException, IOException {
        // Delete all directories matching the pattern /tmp/config-repo-*
        deleteOldConfigRepos("/tmp/config-repo-*");

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String repoDir = "/tmp/config-repo-" + timestamp;
        File file = new File(repoDir);

        Git.cloneRepository()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(GIT_USERNAME, GIT_PASSWORD))
                .setURI(REPO_URL)
                .setDirectory(file)
                .call();

        System.out.println("Repository cloned to: " + repoDir);
    }

    private void deleteOldConfigRepos(String pattern) throws IOException {
        Path dir = Paths.get("/tmp");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "config-repo-*")) {
            for (Path entry : stream) {
                deleteDirectory(entry.toFile());
            }
        }
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
