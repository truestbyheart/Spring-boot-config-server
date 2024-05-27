package com.instrapp.config;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GitCloneScheduler {

    private final GitService gitService;

    public GitCloneScheduler(GitService gitService) {
        this.gitService = gitService;
    }

    @Scheduled(fixedRateString = "${github.repo.clone.interval:3600000}")
    public void cloneRepoPeriodically() {
        try {
            gitService.cloneRepo();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }
}
