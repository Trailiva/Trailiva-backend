package com.trailiva.service;


import com.trailiva.data.model.Priority;
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

import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp(){

    }

    @Test
    void testThatATaskCanBeFilteredByPriority() throws TaskException {
        when(taskRepository.findAll()).thenReturn()
        taskService.filterTaxByPriority(Priority.LOW);
    }
}
