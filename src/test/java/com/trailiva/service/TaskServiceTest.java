package com.trailiva.service;


import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Tab;
import com.trailiva.data.model.Task;
import com.trailiva.data.repository.TaskRepository;
import com.trailiva.web.exceptions.TaskException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task firstTask;
    private Task secondTask;
    @BeforeEach
    void setUp(){
        firstTask = new Task();
        firstTask.setId(1L);
        firstTask.setDescription("first task");
        firstTask.setPriority(Priority.HIGH);
        firstTask.setTab(Tab.IN_PROGRESS);

        secondTask = new Task();
        secondTask.setId(2L);
        secondTask.setDescription("second task");
        secondTask.setPriority(Priority.LOW);
        secondTask.setTab(Tab.PENDING);
    }

    @Test
    void testThatATaskCanBeFilteredByPriority() throws TaskException {
        List<Task> allTask = List.of(firstTask, secondTask);
        when(taskRepository.findAll()).thenReturn(allTask);
        List<Task> filteredTask = taskService.filterTaxByPriority(Priority.LOW);
        assertThat(filteredTask.size()).isEqualTo(1);
        assertThat(filteredTask.get(0).getDescription()).isEqualTo(secondTask.getDescription());
    }

    @Test
    void testThatATaskCanBeFilteredByTab() throws TaskException {
        List<Task> allTask = List.of(firstTask, secondTask);
        when(taskRepository.findAll()).thenReturn(allTask);
        List<Task> filteredTask = taskService.filterTaxByTab(Tab.PENDING);
        assertThat(filteredTask.size()).isEqualTo(1);
        assertThat(filteredTask.get(0).getDescription()).isEqualTo(secondTask.getDescription());
    }
}
