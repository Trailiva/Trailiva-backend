package com.trailiva.service;

import com.trailiva.data.model.Project;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.ProjectRequest;

public interface ProjectService {
    Project createProject(ProjectRequest request, Long workspaceId) throws WorkspaceException, UserException, ProjectException;
    void updateProject(ProjectRequest request, Long projectId);
    void deleteProject(Long projectId);
    Project getProjectById(Long projectId) throws ProjectException;
}
