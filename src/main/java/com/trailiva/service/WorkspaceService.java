package com.trailiva.service;

import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;

import java.util.List;
import java.util.Set;

public interface WorkspaceService {
    Set<WorkSpace> getWorkspaces(Long userId) throws UserException;
    WorkSpace getWorkspace(Long workspaceId) throws WorkspaceException;
    OfficialWorkspace getOfficialWorkspace(Long workspaceId) throws WorkspaceException;
    WorkSpace createWorkspace(WorkspaceRequest request,  Long userId)throws WorkspaceException, UserException;
    WorkSpace addMemberToOfficialWorkspace(Long workspaceId, String memberEmail) throws UserException, WorkspaceException, BadRequestException;
}
