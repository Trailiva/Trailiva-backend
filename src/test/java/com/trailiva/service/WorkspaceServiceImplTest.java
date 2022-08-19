//package com.trailiva.service;
//
//import com.trailiva.data.model.User;
//import com.trailiva.data.model.WorkSpace;
//import com.trailiva.data.repository.UserRepository;
//import com.trailiva.data.repository.WorkspaceRepository;
//import com.trailiva.web.exceptions.UserException;
//import com.trailiva.web.exceptions.WorkspaceException;
//import com.trailiva.web.payload.request.WorkspaceRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.modelmapper.ModelMapper;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//import static com.trailiva.data.model.WorkSpaceType.PERSONAL;
//import static java.util.List.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//
//@Slf4j
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class WorkspaceServiceImplTest {
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @Mock
//    private WorkspaceRepository workspaceRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private WorkspaceServiceImpl workspaceService;
//
//    private WorkspaceRequest workspaceRequest;
//
//    Long workspaceId = 1L;
//    Long userId = 1L;
//    User user;
//
//
//    @BeforeEach
//    void setUp() {
//        workspaceRequest = new WorkspaceRequest();
//        workspaceRequest.setName("Todo App");
//        workspaceRequest.setDescription("Build a todo app");
//        workspaceRequest.setWorkSpaceType(PERSONAL);
//        workspaceRequest.setReferenceName("TD");
//        user = new User();
//        user.setWorkspaces(Set.of(new WorkSpace(), new WorkSpace()));
//    }
//
//    @AfterEach
//    void tearDown() {
//        modelMapper = null;
//        workspaceRepository = null;
//        userRepository = null;
//        workspaceService = null;
//        user = null;
//    }
//
//    @Test
//    void testThat_workSpaceIsCreated() throws UserException, WorkspaceException {
//        //Given
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//        when(workspaceRepository.existsByName(anyString())).thenReturn(false);
//        when(modelMapper.map(workspaceRequest, WorkSpace.class)).thenReturn(new WorkSpace());
//        when(workspaceRepository.save(new WorkSpace())).thenReturn(new WorkSpace());
//        when(userRepository.save(any(User.class))).thenReturn(new User());
//
//        //When
////        workspaceService.createPersonalWorkspace(workspaceRequest, 1L);
//
//        //Assertion
//        verify(userRepository, times(1)).findById(1L);
//        verify(workspaceRepository, times(1)).existsByName(workspaceRequest.getName());
//        verify(modelMapper, times(1)).map(workspaceRequest, WorkSpace.class);
//        verify(workspaceRepository, times(1)).save(new WorkSpace());
//    }
//
////    @Test
////    void throwExceptionWhenWhenUserIdIsNotValid(){
////        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
////        assertThatThrownBy(()-> workspaceService.createPersonalWorkspace(workspaceRequest, 1L))
////                .isInstanceOf(UserException.class)
////                .hasMessage("User not found");
////    }
//
//    @Test
//    void throwExceptionWhenWorkspaceNameAlreadyExit(){
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
//        when(workspaceRepository.existsByName(anyString())).thenReturn(true);
////        assertThatThrownBy(()-> workspaceService.createPersonalWorkspace(workspaceRequest, 1L))
////                .isInstanceOf(WorkspaceException.class)
////                .hasMessage("Workspace with name already exist");
//    }
//
//
//    @Test
//    void userCanGetAllWorkspaceWithUserId() throws UserException {
////        Given
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//
////        When
//        Set<WorkSpace> userWorkspace = workspaceService.getWorkspaces(userId);
//
////        Assert
//        verify(userRepository, times(1)).findById(userId);
//        assertThat(userWorkspace.size()).isEqualTo(2);
//    }
//
//    @Test
//    void throwExceptionWhenUserCanGetAllWorkspaceWithUserIdThatDoesNotValid() throws UserException {
////        Given
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
////        When
//        assertThatThrownBy(()-> workspaceService.getWorkspaces(userId))
//                .isInstanceOf(UserException.class)
//                .hasMessage("User not found");
//    }
//
//
//    @Test
//    void testThatListIsUnModifiableWhenUserGetAllWorkspaceWithUserId() throws UserException {
////        Given
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//
////        When
//        Set<WorkSpace> userWorkspace = workspaceService.getWorkspaces(userId);
//
////        Assert
//        assertThatThrownBy(()-> userWorkspace.add(new WorkSpace())).isInstanceOf(UnsupportedOperationException.class);
//    }
//
//    @Test
//    void userCanGetWorkspaceWithUserId() throws  WorkspaceException {
////        Given
//        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.of(new WorkSpace()));
//
////        When
//        WorkSpace userWorkspace = workspaceService.getOfficialWorkspace(workspaceId);
//
////        Assert
//        verify(workspaceRepository, times(1)).findById(workspaceId);
//        assertThat(userWorkspace).isNotNull();
//    }
//
//
//    @Test
//    void throwExceptionWhenUserCanGetWorkspaceWithUserIdThatDoesNotValid() throws  WorkspaceException {
////        Given
//        when(workspaceRepository.findById(anyLong())).thenReturn(Optional.empty());
//
////        When
//        assertThatThrownBy(() -> workspaceService.getOfficialWorkspace(workspaceId))
//                .isInstanceOf(WorkspaceException.class)
//                .hasMessage("Workspace not found");
//    }
//
//}