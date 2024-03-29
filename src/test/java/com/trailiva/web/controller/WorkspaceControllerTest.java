//package com.trailiva.web.controller;
//
//import com.trailiva.data.model.WorkSpaceType;
//import com.trailiva.service.workspace.WorkspaceService;
//import com.trailiva.web.payload.request.WorkspaceRequest;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.json.JacksonTester;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.web.context.WebApplicationContext;
//
//import static org.assertj.core.api.BDDAssertions.then;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//@ExtendWith(SpringExtension.class)
//@AutoConfigureJsonTesters
//@WebMvcTest(WorkspaceController.class)
//@AutoConfigureMockMvc
//class WorkspaceControllerTest {
//
//    @MockBean
//    private WorkspaceService workspaceService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    protected WebApplicationContext wac;
//
//
//    @Autowired
//    private JacksonTester<WorkspaceRequest> jsonWorkspaceRequest;
//
//    @Autowired
//    private JacksonTester<WorkSpace> jsonWorkspace;
//
//    WorkspaceRequest workspaceRequest;
//
//    WorkSpace workSpace;
//
//    @BeforeEach
//    void setup(){
//
//
//        workspaceRequest = new WorkspaceRequest("Trailiva",
//                "task management software",
//                "TR",
//                WorkSpaceType.PERSONAL);
//
//        workSpace = new WorkSpace();
//        workSpace.setReferenceName(workspaceRequest.getReferenceName());
//        workSpace.setName(workspaceRequest.getName());
//        workSpace.setDescription(workspaceRequest.getDescription());
////        workSpace.setWorkSpaceType(workspaceRequest.getWorkSpaceType());
////        workSpace.setTasks(List.of(new Task(), new Task()));
//    }
//
//    @AfterEach
//    void tearDown() {
//        workspaceService = null;
//        jsonWorkspace = null;
//        jsonWorkspaceRequest = null;
//        mockMvc = null;
//    }
//
////    @Test
////    void whenUserSendRequestToCreateWorkspaceEndPoint_AWorkspaceIsCreated() throws Exception {
////        given(workspaceService.createPersonalWorkspace(eq(workspaceRequest), eq(1L)))
////                .willReturn(workSpace);
////
////        MockHttpServletResponse response = mockMvc
////                .perform(post("/create").contentType(MediaType.APPLICATION_JSON).with((RequestPostProcessor) bearerToken("rrgkjbgkjbg"))
////                .content(jsonWorkspaceRequest.write(workspaceRequest).getJson()))
////        .andReturn().getResponse();
////
////        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
////        then(response.getContentAsString()).isEqualTo(
////                jsonWorkspace.write(workSpace).getJson());
////    }
//}