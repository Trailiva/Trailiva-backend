package com.trailiva.service;

import com.trailiva.data.model.User;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.data.model.WorkSpaceType;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.trailiva.data.model.WorkSpaceType.PERSONAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkspaceServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkspaceServiceImpl workspaceService;

    private WorkspaceRequest workspaceRequest;

    @BeforeEach
    void setUp() {
        workspaceRequest = new WorkspaceRequest();
        workspaceRequest.setName("Todo App");
        workspaceRequest.setDescription("Build a todo app");
        workspaceRequest.setWorkSpaceType(PERSONAL);
        workspaceRequest.setReferenceName("TD");
    }

    @AfterEach
    void tearDown() {
        modelMapper = null;
        workspaceRepository = null;
        userRepository = null;
        workspaceService = null;
    }

    @Test
    void testThat_workSpaceIsCreated() throws UserException, WorkspaceException {
        //Given
        doNothing().when(workspaceService).create(any(WorkspaceRequest.class), anyLong());
        doNothing().when(workspaceRepository).save(new WorkSpace());
        when(workspaceRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(modelMapper.map(any(), User.class)).thenReturn(new User());

        //When
        workspaceService.create(workspaceRequest, 1L);

        //Assertion
        verify(userRepository, times(1)).findById(1L);
        verify(workspaceRepository, times(1)).existsByName(workspaceRequest.getName());
        verify(workspaceRepository, times(1)).save(new WorkSpace());
    }
}