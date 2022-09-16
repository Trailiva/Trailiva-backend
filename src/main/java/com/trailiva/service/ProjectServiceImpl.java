package com.trailiva.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.*;
import com.trailiva.data.repository.*;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.ProjectRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.trailiva.data.model.TokenType.PROJECT_REQUEST;
import static com.trailiva.data.model.TokenType.WORKSPACE_REQUEST;
import static com.trailiva.util.Helper.convertMultiPartToFile;
import static com.trailiva.util.Helper.isValidToken;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final PersonalWorkspaceRepository personalWorkspaceRepository;
    private final OfficialWorkspaceRepository officialWorkspaceRepository;
    private final ProjectRequestTokenRepository projectRequestTokenRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ModelMapper modelMapper, ProjectRepository projectRepository,
                              PersonalWorkspaceRepository personalWorkspaceRepository,
                              OfficialWorkspaceRepository officialWorkspaceRepository,
                              ProjectRequestTokenRepository projectRequestTokenRepository,
                              UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.projectRepository = projectRepository;
        this.personalWorkspaceRepository = personalWorkspaceRepository;
        this.officialWorkspaceRepository = officialWorkspaceRepository;
        this.projectRequestTokenRepository = projectRequestTokenRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Project createProjectForPersonalWorkspace(ProjectRequest request, Long workspaceId) throws WorkspaceException, ProjectException {
        PersonalWorkspace workSpace = personalWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace does not exist"));
        List<Project> projects = workSpace.getProjects();


        if (isValidProject(projects, request.getName())) {
            Project project = modelMapper.map(request, Project.class);
            project.setReferenceName(generateReferenceName(request.getName()));
            project.setCreator(workSpace.getCreator());
            Project savedProject = projectRepository.save(project);
            workSpace.addProject(savedProject);
            personalWorkspaceRepository.save(workSpace);
            return savedProject;
        } else throw new ProjectException("Project name already exist on your project list");
    }

    @Override
    public Project createProjectForOfficialWorkspace(ProjectRequest request, Long workspaceId) throws WorkspaceException, ProjectException {
        OfficialWorkspace workSpace = officialWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace does not exist"));
        List<Project> projects = workSpace.getProjects();


        if (isValidProject(projects, request.getName())) {
            Project project = modelMapper.map(request, Project.class);
            project.setReferenceName(generateReferenceName(request.getName()));
            project.setCreator(workSpace.getCreator());
            Project savedProject = projectRepository.save(project);
            workSpace.addProject(savedProject);
            officialWorkspaceRepository.save(workSpace);
            return savedProject;
        } else throw new ProjectException("Project name already exist on your project list");
    }

    @Override
    public void updateProject(ProjectRequest request, Long projectId) {

    }

    @Override
    public void deleteProject(Long projectId) {

    }

    @Override
    public Project getProjectById(Long projectId) throws ProjectException {
        return projectRepository.findById(projectId).orElseThrow(() -> new ProjectException("Project not found"));
    }

    @Override
    public List<Task> getUserTasks(Long projectId, Long memberId) throws ProjectException {
        Project project = getProjectById(projectId);
        List<Task> tasks;
        tasks = project.getTasks().stream()
                .filter(task -> task.getAssignee().getUserId().equals(memberId))
                .collect(Collectors.toList());
        return tasks;
    }

    @Override
    public int countProjectTask(Long projectId) throws ProjectException {
        Project project = getProjectById(projectId);
        return project.getTasks().size();
    }


    @Override
    public void addContributor(List<String> contributorEmails, String userEmail) throws UserException, ProjectException {
        if (!contributorEmails.isEmpty()) {
            for (String email : contributorEmails) {
                sendRequestToken(userEmail, email);
            }
        }
    }

    @Override
    public void addContributorFromCSV(MultipartFile file, String userEmail) throws IOException, CsvValidationException, UserException, ProjectException {
        CSVReader reader = new CSVReader(new FileReader(convertMultiPartToFile(file)));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            for (String email : nextLine) {
                sendRequestToken(userEmail, email);
            }
        }
    }

    @Override
    public void addContributor(String requestToken) throws TokenException, UserException {
        ProjectRequestToken token = getToken(requestToken, PROJECT_REQUEST.toString());
        if (isValidToken(token.getExpiryDate())) throw new TokenException("Token has expired");
        onboardContributor(token.getProject(), token.getUser());
        projectRequestTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public List<Task> getTasksByProjectId(Long projectId) throws ProjectException {
        Project project = projectRepository.findById(projectId).orElseThrow(
                ()-> new ProjectException("Project not found"));
        return project.getTasks();
    }


    private ProjectRequestToken getToken(String requestToken, String tokenType) throws TokenException {
        return projectRequestTokenRepository.findByTokenAndTokenType(requestToken, tokenType)
                .orElseThrow(() -> new TokenException("Invalid token"));
    }

    private void onboardContributor(Project project, User contributor) throws UserException {
        if (userAlreadyExistInWorkspace(project.getContributors(), contributor.getEmail(), project.getCreator().getEmail())) {
            throw new UserException("Contributor with email " + contributor.getEmail() + " already added to this project");
        }
        project.getContributors().add(contributor);
        saveProject(project);
    }

    private boolean userAlreadyExistInWorkspace(Set<User> contributors, String contributorEmail, String creatorEmail) {
        boolean contributorExist = contributors.stream().anyMatch(user -> user.getEmail().equals(contributorEmail));
        return contributorExist || creatorEmail.equals(contributorEmail);
    }

    private Project saveProject(Project project) {
       return projectRepository.save(project);
    }


    private void sendRequestToken(String userEmail, String email) throws UserException, ProjectException {
        User user = getAUserByEmail(email);
        String token = UUID.randomUUID().toString();
        Project project = getUserProject(userEmail);

        ProjectRequestToken requestToken = new ProjectRequestToken(token, user,
                PROJECT_REQUEST.toString(), project);
        projectRequestTokenRepository.save(requestToken);
//        emailService.sendWorkspaceRequestTokenEmail(email, requestToken.getToken());
    }

    private Project getUserProject(String email) throws UserException, ProjectException {
        User creator = getAUserByEmail(email);
        return projectRepository.findByCreator(creator)
                .orElseThrow(() -> new ProjectException("Project not found"));
    }

    private User getAUserByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
    }


    private boolean isValidProject(List<Project> projects, String name) {
        return projects.stream().noneMatch(project -> project.getName().equalsIgnoreCase(name));
    }

    private String generateReferenceName(String projectName) {
        return projectName.substring(0, 2).toUpperCase();
    }
}
