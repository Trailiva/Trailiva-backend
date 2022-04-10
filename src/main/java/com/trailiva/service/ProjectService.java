package com.trailiva.service;

import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.ProjectRequest;

public interface ProjectService {
    void createProject(ProjectRequest request, Long workspaceId) throws WorkspaceException, UserException, ProjectException;
    void updateProject(ProjectRequest request, Long projectId);
    void deleteProject(Long projectId);
}
