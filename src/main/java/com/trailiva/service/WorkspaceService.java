package com.trailiva.service;

import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;

public interface WorkspaceService {
    WorkSpace getUserPersonalWorkspace(Long userId) throws UserException;
    WorkSpace getPersonalWorkspace(Long workspaceId) throws WorkspaceException;
    WorkSpace getOfficialWorkspace(Long workspaceId) throws WorkspaceException;
    WorkSpace getUserOfficialWorkspace(Long userId) throws UserException, WorkspaceException;
    WorkSpace createWorkspace(WorkspaceRequest request,  Long userId)throws WorkspaceException, UserException;
    OfficialWorkspace addMemberToOfficialWorkspace(String memberEmail, Long userId) throws UserException, WorkspaceException, BadRequestException;
    WorkSpace addModeratorToOfficialWorkspace(String memberEmail, Long userId) throws UserException, WorkspaceException;
}
