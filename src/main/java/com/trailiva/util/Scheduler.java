package com.trailiva.util;

import com.trailiva.data.model.Task;
import com.trailiva.data.repository.TaskRepository;
import com.trailiva.data.repository.TokenRepository;
import com.trailiva.service.AuthService;
import com.trailiva.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Component
@EnableScheduling
@Slf4j
public class Scheduler {

    private final TokenRepository tokenRepository;
    private final TaskRepository taskRepository;

    public Scheduler(TokenRepository tokenRepository, TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void verifyDueTask() {
        log.info("============ TASK UPDATE SCHEDULER STARTED ===========");
        taskRepository.updateDueTask();
        log.info("============ TASK UPDATE SCHEDULER ENDED ===========");
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void getExpiredToken(){
        log.info("============ TOKEN SCHEDULER STARTED ===========");
        tokenRepository.deleteExpiredToken();
        log.info("============ TOKEN SCHEDULER ENDED ===========");
    }
}
