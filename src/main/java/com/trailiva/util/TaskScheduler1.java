package com.trailiva.util;

import com.trailiva.data.model.Task;
import com.trailiva.service.TaskService;
import com.trailiva.service.TaskServiceImpl;
import com.trailiva.web.exceptions.TaskException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Component
@EnableScheduling
@Slf4j
public class TaskScheduler1 {
    @Autowired
    TaskService taskService;
    @Scheduled(cron = "0 8 * * *")
    private void verifyDueTask() throws TaskException {
        List<Task> dueTask = taskService.getDueTasks(LocalDate.now());
        dueTask.forEach(task -> changeTaskStatus(task));
        log.info("Hello world");
    }
    private void changeTaskStatus(Task task) {
        task.setDue(true);
    }
}
