package com.trailiva.service.workspace;

import com.trailiva.data.model.PersonalWorkspace;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;

import java.util.List;

public interface PersonalWorkspaceService {
    PersonalWorkspace createPersonalWorkspace(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException;

    PersonalWorkspace getUserWorkspace(Long userId) throws UserException, WorkspaceException;

    PersonalWorkspace getWorkspace(Long workspaceId) throws WorkspaceException;

    int countWorkspaceProjects(Long workspaceId) throws WorkspaceException;

    List<PersonalWorkspace> getAllWorkspace();
}
