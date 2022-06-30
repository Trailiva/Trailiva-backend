package com.trailiva.util;

import com.trailiva.data.model.Task;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DURATION;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TrailivaTaskSchedulerTest {
    @Mock
    TaskRepository taskRepository;
    @InjectMocks
    TaskService mockTaskService=new TaskServiceImpl();

    @Mock
    TrailivaTaskScheduler trailivaTaskScheduler;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testThatDueTasksCanBeGotton() throws InterruptedException{
        when(mockTaskService.getDueTasks(any(LocalDate.class))).thenReturn(anyList());
        await()
                .atMost(Duration.ofSeconds(10L))
                .untilAsserted(()->verify(trailivaTaskScheduler, atLeast(1)).verifyDueTask());
    }
}