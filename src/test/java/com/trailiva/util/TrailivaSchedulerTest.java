package com.trailiva.util;

import com.trailiva.data.repository.TaskRepository;
import com.trailiva.service.TaskService;
import com.trailiva.service.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Duration;
import java.time.LocalDate;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TrailivaSchedulerTest {
    @Mock
    TaskRepository taskRepository;
    @InjectMocks
    TaskService mockTaskService=new TaskServiceImpl(null, null,  null);

    @Mock
    Scheduler scheduler;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testThatDueTasksCanBeGotten() {
        when(mockTaskService.getDueTasks(any(LocalDate.class))).thenReturn(anyList());
        await()
                .atMost(Duration.ofSeconds(10L))
                .untilAsserted(()->verify(scheduler, atLeast(1)).verifyDueTask());
    }
}