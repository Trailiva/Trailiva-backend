package com.trailiva.service;

import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.data.model.PersonalWorkspace;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WorkspaceService {

    PersonalWorkspace getUserPersonalWorkspace(Long userId) throws UserException, WorkspaceException;

    WorkSpace getPersonalWorkspace(Long workspaceId) throws WorkspaceException;

    WorkSpace getOfficialWorkspace(Long workspaceId) throws WorkspaceException;

    OfficialWorkspace getUserOfficialWorkspace(Long userId) throws UserException, WorkspaceException;

    WorkSpace createWorkspace(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException;

    void addContributorToOfficialWorkspace(List<String> memberEmail, Long userId) throws UserException, WorkspaceException;

    void addModeratorToOfficialWorkspace(List<String> moderatorEmail, Long userId) throws UserException, WorkspaceException;

    void addModeratorToWorkspaceFromCSV(MultipartFile file, Long userId) throws IOException, CsvValidationException, UserException, WorkspaceException;

    void addContributorToWorkspaceFromCSV(MultipartFile file, Long userId) throws IOException, CsvValidationException, UserException, WorkspaceException;

    void addContributorToWorkspace(String requestToken) throws TokenException, UserException;

    void addModeratorToWorkspace(String requestToken) throws TokenException, UserException;

    void removeContributorFromWorkspace(Long userId, Long memberId) throws UserException, WorkspaceException;
    
    void removeModeratorFromWorkspace(Long userId, Long moderatorId) throws UserException, WorkspaceException;
}
