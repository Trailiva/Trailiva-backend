package com.trailiva.scheduler;

import com.trailiva.data.repository.WorkspaceRequestTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class TokenScheduler {

    private final WorkspaceRequestTokenRepository workspaceRequestTokenRepository;

    public TokenScheduler(WorkspaceRequestTokenRepository workspaceRequestTokenRepository) {
        this.workspaceRequestTokenRepository = workspaceRequestTokenRepository;
    }

//    @Scheduled(cron="0 1 0 * * *") //00:01 every day
//    @Scheduled(cron="1 * * * * *") //00:01 every day
    public void deleteInvalidToken(){
    }
}
