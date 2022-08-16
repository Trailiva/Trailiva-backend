package com.trailiva.service;

import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;

import java.util.List;

public interface WorkspaceService {
    WorkSpace getUserPersonalWorkspace(Long userId) throws UserException;
    WorkSpace getPersonalWorkspace(Long workspaceId) throws WorkspaceException;
    WorkSpace getOfficialWorkspace(Long workspaceId) throws WorkspaceException;
    WorkSpace getUserOfficialWorkspace(Long userId) throws UserException, WorkspaceException;
    WorkSpace createWorkspace(WorkspaceRequest request,  Long userId)throws WorkspaceException, UserException;
    void addMemberToOfficialWorkspace(List<String> memberEmail, Long userId) throws UserException, WorkspaceException, BadRequestException;
    void addModeratorToOfficialWorkspace(List<String> moderatorEmail, Long userId) throws UserException, WorkspaceException;
}
