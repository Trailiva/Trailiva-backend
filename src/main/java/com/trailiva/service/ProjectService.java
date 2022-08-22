package com.trailiva.service;

import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.Project;
import com.trailiva.data.model.Task;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.ProjectRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProjectService {
    Project createProjectForPersonalWorkspace(ProjectRequest request, Long workspaceId) throws WorkspaceException, UserException, ProjectException;

    Project createProjectForOfficialWorkspace(ProjectRequest request, Long workspaceId) throws WorkspaceException, ProjectException;

    void updateProject(ProjectRequest request, Long projectId);

    void deleteProject(Long projectId);

    Project getProjectById(Long projectId) throws ProjectException;

    List<Task> getUserTasks(Long projectId, Long memberId) throws UserException, ProjectException;

    int countProjectTask(Long projectId) throws ProjectException;

    void addContributor(List<String>contributorEmails, String userEmail) throws UserException, ProjectException;

    void addContributorFromCSV(MultipartFile file, String userEmail) throws IOException, CsvValidationException, UserException, ProjectException;

    void addContributor(String requestToken) throws TokenException, UserException;
}
