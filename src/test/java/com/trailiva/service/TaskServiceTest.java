package com.trailiva.service;


import com.sun.xml.bind.v2.TODO;
import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Tab;
import com.trailiva.data.model.Task;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.data.repository.TaskRepository;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    WorkspaceRepository workspaceRepository;

    @Mock
    private ModelMapper modelMapper;


    @InjectMocks
    private TaskServiceImpl taskService;

    private Task firstTask;
    private Task secondTask;
    private WorkSpace mockedWorkSpace;
    private TaskRequest taskRequest;
    private List<Task> listOfTask = new ArrayList<>();

    @BeforeEach
    void setUp(){
        firstTask = new Task();
        firstTask.setId(1L);
        firstTask.setDescription("first task");
        firstTask.setPriority(Priority.HIGH);
        firstTask.setTab(Tab.IN_PROGRESS);
        firstTask.setName("first task");

        secondTask = new Task();
        secondTask.setId(2L);
        secondTask.setDescription("second task");
        secondTask.setPriority(Priority.LOW);
        secondTask.setTab(Tab.PENDING);
        secondTask.setName("second task");

        mockedWorkSpace = new WorkSpace();
        mockedWorkSpace.setWorkspaceId(1L);
        mockedWorkSpace.setReferenceName("referenced workspace");
        listOfTask.add(firstTask);
        listOfTask.add(secondTask);
        mockedWorkSpace.setTasks(listOfTask);

        taskRequest= new TaskRequest();
        taskRequest.setDescription("test task");
        taskRequest.setName("first task request");
    }

    @Test
    void testThatTaskCanBeCreated() throws TaskException, WorkspaceException {
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.of(mockedWorkSpace));

        when(modelMapper.map(taskRequest, Task.class)).thenReturn(secondTask);
        when(taskRepository.save(any(Task.class))).thenReturn(secondTask);
        when(workspaceRepository.save(any(WorkSpace.class))).thenReturn(mockedWorkSpace);
        taskService.createTask(taskRequest, 1L);

        verify(workspaceRepository, times(1)).findById(mockedWorkSpace.getWorkspaceId());
        verify(taskRepository, times(1)).save(secondTask);
    }

    
    @Test
    void testThatTaskCanBeUpdated() throws TaskException {
        Task updatedTask = new Task();
        updatedTask.setDescription(taskRequest.getDescription());
        when(taskRepository.findById(1L)).thenReturn(Optional.of(new Task()));
        doNothing().when(modelMapper).map(updatedTask, new Task());
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        taskService.updateTask(taskRequest, 1L);
        assertThat(updatedTask.getDescription()).isEqualTo("test task");
    }

    @Test
    void testThatWhenTaskDoesNotExistThrowsExcepion() throws TaskException {
        Task updatedTask = new Task();
        updatedTask.setDescription(taskRequest.getDescription());
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> taskService.updateTask(taskRequest, anyLong()))
                .isInstanceOf(TaskException.class)
                .hasMessage("Task does not exist");
    }


    @Test
    void testThatTaskCanBeDeleted() throws TaskException {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(firstTask));
        doNothing().when(taskRepository).delete(firstTask);
        taskService.deleteTask(firstTask.getId());
        assertThat(taskRepository.findById(anyLong())).isNotPresent();
    }

    @Test
    void testThatWhenTaskDoesNotExistThrowException(){
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(()-> taskService.deleteTask(anyLong()))
                .isInstanceOf(TaskException.class)
                .hasMessage("Task not found");
    }

    @Test
    void testThatTaskCanBeRetreivedByWorkspace() throws WorkspaceException {
        when(workspaceRepository.findById(1L)).thenReturn(Optional.of(mockedWorkSpace));
        List<Task> workspacetasks = taskService.getTasksByWorkspaceId(1L);
        assertThat(workspacetasks.size()).isEqualTo(2);
    }

    @Test
    void testThatWhenTaskIsNotFoundThrowsException(){
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(()-> taskService.getTasksByWorkspaceId(7L))
                .isInstanceOf(WorkspaceException.class)
                .hasMessage("No workspace found");
    }

    @Test
    void testThatTaskDetailsCanBeRetreived() throws WorkspaceException {
        when(workspaceRepository.findById(1L)).thenReturn(Optional.of(mockedWorkSpace));
        Task taskDetails = taskService.getTaskDetail(1L, 1L);
        assertThat(mockedWorkSpace.getWorkspaceId()).isEqualTo(1L);
        assertThat(taskDetails.getName()).isEqualTo("first task");
    }


    @Test
    void testThatWhenTaskDoesNotExistThrowsException(){
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(()-> taskService.getTaskDetail(2L, 1L))
                .isInstanceOf(WorkspaceException.class)
                .hasMessage("No workspace found");
    }


    @Test
    void testThatTaskTagCanBeUpdated() throws TaskException {

        Task updatedTask = new Task();
        updatedTask.setTab(Tab.COMPLETED);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(firstTask));

        when(taskRepository.save(firstTask)).thenReturn(updatedTask);
        taskService.updateTaskTag(1L,  "COMPLETED");
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
        assertThat(updatedTask.getTab().toString()).isEqualTo("COMPLETED");
    }

    @Test
    void testThatWhenTaskDoesNotExistThrowExeption(){
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(()-> taskService.updateTaskTag(12L, "COMPLETED"))
                .isInstanceOf(TaskException.class)
                .hasMessage("Task not found");
    }

    @Test
    void testThatATaskCanBeFilteredByPriority() throws WorkspaceException {
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockedWorkSpace));
        List<Task> filteredTask = taskService.filterTaskByPriority(1L, Priority.LOW);
        verify(workspaceRepository, times(1)).findById(1L);
        assertThat(filteredTask.size()).isEqualTo(1);
        assertThat(filteredTask.get(0).getDescription()).isEqualTo("second task");
    }

    @Test
    void testThatIfWorkspaceIsNotFound_ThrowException() {
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(()-> taskService.filterTaskByPriority(2L, Priority.LOW))
                .isInstanceOf(WorkspaceException.class).hasMessage("No workspace found");
    }

    @Test
    void testThatWhenTaskFilteredByPriority_IsNotFound_ReturnsAnEmptyList() throws WorkspaceException {
       WorkSpace workSpace = new WorkSpace();
       workSpace.setTasks(List.of(firstTask));
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.of(workSpace));
        List<Task> filteredTask = taskService.filterTaskByPriority(1L, Priority.LOW);
        assertThat(filteredTask.size()).isEqualTo(0);
    }

    @Test
    void testThatATaskCanBeFilteredByTab() throws WorkspaceException {
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockedWorkSpace));
        List<Task> filteredTask = taskService.filterTaskByTab(1L, Tab.PENDING);
        verify(workspaceRepository, times(1)).findById(1L);
        assertThat(filteredTask.size()).isEqualTo(1);
        assertThat(filteredTask.get(0).getDescription()).isEqualTo("second task");
    }

    @Test
    void testThatIfWorkspaceForFiterByTabIsNotFound_ThrowException() {
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(()-> taskService.filterTaskByTab(2L, Tab.PENDING))
                .isInstanceOf(WorkspaceException.class).hasMessage("No workspace found");
    }

    @Test
    void testThatWhenTaskFilteredByTab_IsNotFound_ReturnsAnEmptyList() throws WorkspaceException {
        WorkSpace workSpace = new WorkSpace();
        workSpace.setTasks(List.of(firstTask));
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.of(workSpace));
        List<Task> filteredTask = taskService.filterTaskByTab(1L, Tab.COMPLETED);
        assertThat(filteredTask.size()).isEqualTo(0);
    }

    @Test
    void testThatListIsUnModifiableWhenFilteredByTab() throws WorkspaceException {
      when(workspaceRepository.findById(anyLong())).thenReturn(Optional.of(mockedWorkSpace));
        List<Task> filteredTask = taskService.filterTaskByTab(1L, Tab.PENDING);
        assertThatThrownBy(()-> filteredTask.add(new Task())).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testThatListIsUnModifiableWhenFilteredByPriority() throws WorkspaceException {
        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.of(mockedWorkSpace));
        List<Task> filteredTask = taskService.filterTaskByPriority(1L, Priority.LOW);
        assertThatThrownBy(()-> filteredTask.add(new Task())).isInstanceOf(UnsupportedOperationException.class);
    }

}
