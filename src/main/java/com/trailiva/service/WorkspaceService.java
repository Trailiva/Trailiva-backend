package com.trailiva.service;

import com.trailiva.data.model.WorkSpace;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;

import java.util.List;

public interface WorkspaceService {
    WorkSpace createWorkspace(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException;
    List<WorkSpace> getWorkspaces(Long userId) throws UserException;
    WorkSpace getWorkspace(Long workspaceId) throws WorkspaceException;
}
