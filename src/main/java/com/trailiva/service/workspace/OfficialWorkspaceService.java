package com.trailiva.service.workspace;

import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.AssignTaskRequest;
import com.trailiva.web.payload.request.WorkspaceRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OfficialWorkspaceService {
    OfficialWorkspace createOfficialWorkspace(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException;

    OfficialWorkspace getOfficialWorkspace(Long workspaceId) throws WorkspaceException;

    OfficialWorkspace getUserOfficialWorkspace(Long userId) throws UserException, WorkspaceException;

    void addContributor(List<String> contributorEmail, Long userId) throws UserException, WorkspaceException;

    void addModerator(List<String> moderatorEmail, Long userId) throws UserException, WorkspaceException;

    void addModeratorFromCSV(MultipartFile file, Long userId) throws IOException, CsvValidationException, UserException, WorkspaceException;

    void addContributorFromCSV(MultipartFile file, Long userId) throws IOException, CsvValidationException, UserException, WorkspaceException;

    void addContributor(String requestToken) throws TokenException, UserException;

    void addModerator(String requestToken) throws TokenException, UserException;

    void removeContributor(Long userId, Long contributorId) throws UserException, WorkspaceException;

    void removeModerator(Long userId, Long moderatorId) throws UserException, WorkspaceException;

    int countContributor(Long workspaceId) throws WorkspaceException;

    int countModerator(Long workspaceId) throws WorkspaceException;

    int countProject(Long workspaceId) throws WorkspaceException;

    List<OfficialWorkspace> getWorkspace();

    void assignContributorToTask(AssignTaskRequest request, Long moderatorId) throws WorkspaceException, TaskException, UserException;

    void requestTask(Long workspaceId, Long taskId, Long contributorId) throws UserException, WorkspaceException, TaskException;

    void assignTaskToContributorWithRequestToken(Long moderatorId, String requestToken) throws TokenException, TaskException, UserException;
}
