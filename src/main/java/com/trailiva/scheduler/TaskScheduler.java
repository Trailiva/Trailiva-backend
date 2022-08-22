package com.trailiva.scheduler;

import com.trailiva.data.model.Task;
import com.trailiva.data.repository.TaskRepository;
import com.trailiva.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
@EnableScheduling
@Slf4j
public class TaskScheduler {
    private final TaskService taskService;
    private final TaskRepository taskRepository;

    public TaskScheduler(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    @Scheduled(cron="0 1 0 * * *") //00:01 every day
    public void verifyDueTask() {
        List<Task> dueTask = taskService.getDueTasks(LocalDate.now());
        dueTask.forEach(task -> {
            changeTaskStatus(task);
            taskRepository.save(task);
        });
    }

    private void changeTaskStatus(Task task) {
        task.setElapse(true);
    }
}
