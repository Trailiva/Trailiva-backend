package com.trailiva.service.workspace;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.*;
import com.trailiva.data.repository.*;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.AssignTaskRequest;
import com.trailiva.web.payload.request.WorkspaceRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.trailiva.data.model.TokenType.TASK_REQUEST;
import static com.trailiva.data.model.TokenType.WORKSPACE_REQUEST;
import static com.trailiva.util.Helper.convertMultiPartToFile;
import static com.trailiva.util.Helper.isValidToken;

@Service
public class OfficialWorkspaceServiceImpl implements OfficialWorkspaceService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OfficialWorkspaceRepository officialWorkspaceRepository;
    private final WorkspaceRequestTokenRepository workspaceRequestTokenRepository;
    private final TaskRequestTokenRepository taskRequestTokenRepository;
    private final TaskRepository taskRepository;

    public OfficialWorkspaceServiceImpl(
            ModelMapper modelMapper, UserRepository userRepository,
            RoleRepository roleRepository, OfficialWorkspaceRepository officialWorkspaceRepository,
            WorkspaceRequestTokenRepository workspaceRequestTokenRepository,
            TaskRequestTokenRepository taskRequestTokenRepository,
            TaskRepository taskRepository) {

        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.officialWorkspaceRepository = officialWorkspaceRepository;
        this.workspaceRequestTokenRepository = workspaceRequestTokenRepository;
        this.taskRequestTokenRepository = taskRequestTokenRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public OfficialWorkspace createOfficialWorkspace(WorkspaceRequest request, Long userId) throws UserException, WorkspaceException {
        User user = getAUserById(userId);
        if (existByName(request.getName()))
            throw new WorkspaceException("Workspace with name already exist");
        user.getRoles().add(roleRepository.findByName("ROLE_SUPER_MODERATOR").get());
        OfficialWorkspace workSpace = modelMapper.map(request, OfficialWorkspace.class);
        OfficialWorkspace saveWorkspace = saveOfficialWorkspace(workSpace);
        user.setOfficialWorkspace(saveWorkspace);
        userRepository.save(user);
        return saveWorkspace;
    }

    private boolean existByName(String name) {
        return officialWorkspaceRepository.existsByName(name);
    }

    @Override
    public OfficialWorkspace getOfficialWorkspace(Long workspaceId) throws WorkspaceException {
        return officialWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public OfficialWorkspace getUserOfficialWorkspace(Long userId) throws WorkspaceException, UserException {
        User creator = getAUserById(userId);
        return Optional.of(creator.getOfficialWorkspace())
                .orElseThrow(() -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public void addContributor(List<String> contributorEmails, Long userId) throws UserException, WorkspaceException {
        if (!contributorEmails.isEmpty()) {
            for (String email : contributorEmails) {
                sendRequestToken(userId, email);
            }
        }

    }

    @Override
    public void addModerator(List<String> moderatorEmail, Long userId) throws UserException, WorkspaceException {
        if (!moderatorEmail.isEmpty()) {
            for (String email : moderatorEmail) {
                sendRequestToken(userId, email);
            }
        }
    }

    @Override
    public void addModeratorFromCSV(MultipartFile file, Long userId) throws IOException,
            CsvValidationException, UserException, WorkspaceException {
        CSVReader reader = new CSVReader(new FileReader(convertMultiPartToFile(file)));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            for (String email : nextLine) {
                sendRequestToken(userId, email);
            }
        }
    }

    @Override
    public void addContributorFromCSV(MultipartFile file, Long userId) throws IOException,
            CsvValidationException, UserException, WorkspaceException {
        CSVReader reader = new CSVReader(new FileReader(convertMultiPartToFile(file)));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            for (String email : nextLine) {
                sendRequestToken(userId, email);
            }
        }
    }

    @Override
    public void addContributor(String requestToken) throws TokenException, UserException {
        WorkspaceRequestToken token = getWorkspaceRequestToken(requestToken, WORKSPACE_REQUEST.toString());
        if (isValidToken(token.getExpiryDate())) throw new TokenException("Token has expired");
        onboardContributor(token.getWorkspace(), token.getUser());
        workspaceRequestTokenRepository.delete(token);
    }

    @Override
    public void addModerator(String requestToken) throws TokenException, UserException {
        WorkspaceRequestToken token = getWorkspaceRequestToken(requestToken, WORKSPACE_REQUEST.toString());
        if (isValidToken(token.getExpiryDate())) throw new TokenException("Token has expired");
        onboardModerator(token.getWorkspace(), token.getUser());
        workspaceRequestTokenRepository.delete(token);
    }


    @Override
    public void removeContributor(Long userId, Long contributorId) throws UserException, WorkspaceException {
        OfficialWorkspace workspace = getUserOfficialWorkspace(userId);
        User user = getAUserById(contributorId);
        workspace.getContributors().remove(user);
    }

    @Override
    public void removeModerator(Long userId, Long moderatorId) throws UserException, WorkspaceException {
        OfficialWorkspace workspace = getUserOfficialWorkspace(userId);
        User moderator = getAUserById(moderatorId);
        workspace.getModerators().remove(moderator);
    }

    @Override
    public int countContributor(Long workspaceId) throws WorkspaceException {
        OfficialWorkspace workspace = getOfficialWorkspace(workspaceId);
        return workspace.getContributors().size();
    }

    @Override
    public int countProject(Long workspaceId) throws WorkspaceException {
        OfficialWorkspace workspace = getOfficialWorkspace(workspaceId);
        return workspace.getProjects().size();
    }

    @Override
    public int countModerator(Long workspaceId) throws WorkspaceException {
        OfficialWorkspace workspace = getOfficialWorkspace(workspaceId);
        return workspace.getModerators().size();
    }

    @Override
    public List<OfficialWorkspace> getWorkspace() {
        return officialWorkspaceRepository.findAll();
    }


    @Override
    public void assignContributorToTask(AssignTaskRequest request, Long moderatorId) throws WorkspaceException, TaskException, UserException {
        OfficialWorkspace workspace = getOfficialWorkspace(request.getWorkspaceId());
        boolean isValidModerator = isValidMember(workspace.getModerators(), moderatorId);
        boolean isValidContributor = isValidMember(workspace.getContributors(), request.getContributorId());

        if (isValidContributor && isValidModerator) {
            assignTask(moderatorId, request.getContributorId(), request.getTaskId());
        } else throw new WorkspaceException("Not a valid member");
    }

    @Override
    public void requestTask(Long workspaceId, Long taskId, Long contributionId) throws UserException, WorkspaceException, TaskException {
        User user = getAUserById(contributionId);
        String token = UUID.randomUUID().toString();
        OfficialWorkspace workspace = getOfficialWorkspace(workspaceId);
        Task task = getATaskById(taskId);
        task.setRequested(true);

        taskRepository.save(task);
        TaskRequestToken requestToken = new TaskRequestToken(token, user, TASK_REQUEST.toString(), task);
        taskRequestTokenRepository.save(requestToken);



        /*
         * Send mail to all contributor on workspace
         *         workspace.getModerators().forEach(moderator -> {
         *
         *   });
         */

    }

    @Override
    public void assignTaskToContributorWithRequestToken(Long moderatorId, String requestToken) throws
            TokenException, TaskException, UserException {
        TaskRequestToken token = taskRequestTokenRepository.findByTokenAndTokenType(requestToken,
                TASK_REQUEST.toString()).orElseThrow(() -> new TokenException("Token is invalid"));
        if (isValidToken(token.getExpiryDate())) throw new TokenException("Token has expired");
        assignTask(moderatorId, token.getUser().getUserId(), token.getTask().getId());
        taskRequestTokenRepository.delete(token);
    }

    private void assignTask(Long moderatorId, Long contributorId, Long taskId) throws TaskException, UserException {
        User assignee = getAUserById(contributorId);
        User reporter = getAUserById(moderatorId);

        Task task = getATaskById(taskId);
        task.setAssignee(assignee);
        task.setAssigned(true);
        task.setReporter(reporter);
        taskRepository.save(task);
    }

    private Task getATaskById(Long taskId) throws TaskException {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new TaskException("Task not found"));
    }

    private WorkspaceRequestToken getWorkspaceRequestToken(String token, String tokenType) throws TokenException {
        return workspaceRequestTokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new TokenException("Invalid token"));
    }

    private void onboardContributor(OfficialWorkspace workspace, User User) throws UserException {
        boolean isAContributor = validateUserOnSpace(workspace.getContributors(), User.getEmail());
        boolean isAModerator = validateUserOnSpace(workspace.getModerators(), User.getEmail());
        if (isAContributor || isAModerator) {
            throw new UserException("Contributor with email " + User.getEmail() + " already added to this workspace");
        }
        workspace.getContributors().add(User);
        saveOfficialWorkspace(workspace);
    }

    private void onboardModerator(OfficialWorkspace workspace, User moderator) throws UserException {
        boolean isAContributor = validateUserOnSpace(workspace.getContributors(), moderator.getEmail());
        boolean isAModerator = validateUserOnSpace(workspace.getModerators(), moderator.getEmail());
        if (isAContributor || isAModerator) {
            throw new UserException("Contributor with email " + moderator.getEmail() + " already added to this workspace");
        }
        moderator.getRoles().add(roleRepository.findByName("ROLE_MODERATOR").get());
        workspace.getModerators().add(moderator);
        saveOfficialWorkspace(workspace);
    }


    private User getAUserById(Long id) throws UserException {
        return userRepository.findById(id).orElseThrow(() -> new UserException("User not found"));
    }

    private OfficialWorkspace saveOfficialWorkspace(OfficialWorkspace workSpace) {
        return officialWorkspaceRepository.save(workSpace);
    }

    private static boolean validateUserOnSpace(Set<User> workspace, String email) {
        return workspace.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isValidMember(Set<User> workspaceUsers, Long memberId) {
        return workspaceUsers.stream().anyMatch(moderator -> moderator.getUserId().equals(memberId));
    }


    private void sendRequestToken(Long userId, String email) throws UserException, WorkspaceException {
        User user = getAUserById(userId);
        String token = UUID.randomUUID().toString();
        OfficialWorkspace workspace = user.getOfficialWorkspace();

        WorkspaceRequestToken requestToken = new WorkspaceRequestToken(token, user, WORKSPACE_REQUEST.toString(),
                workspace);
        workspaceRequestTokenRepository.save(requestToken);
//        emailService.sendWorkspaceRequestTokenEmail(email, requestToken.getToken());
    }
}
