package com.trailiva.util;

import com.trailiva.data.model.Task;
import com.trailiva.service.AuthService;
import com.trailiva.service.TaskService;
import com.trailiva.web.exceptions.TaskException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
@EnableScheduling
@Slf4j
public class TaskScheduler {
    @Autowired
    TaskService taskService;

    @Autowired
    AuthService authService;

    @Scheduled(cron = "0 12 * * * ?")
    public void verifyDueTask() throws TaskException {
        List<Task> dueTask = taskService.getDueTasks(LocalDate.now());
        //noinspection Convert2MethodRef
        dueTask.forEach(task -> changeTaskStatus(task));
    }
    private void changeTaskStatus(Task task) {
        task.setElapse(true);
    }

    @Scheduled(cron = "0 12 * * * ?")
    public void getExpiredToken() throws TaskException {
        authService.deleteExpiredToken();
    }
}
