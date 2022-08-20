package com.trailiva.service;

import com.trailiva.data.model.*;
import com.trailiva.data.repository.OfficialWorkspaceRepository;
import com.trailiva.data.repository.ProjectRepository;
import com.trailiva.data.repository.PersonalWorkspaceRepository;
import com.trailiva.security.UserPrincipal;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.ProjectRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final PersonalWorkspaceRepository personalWorkspaceRepository;
    private final OfficialWorkspaceRepository officialWorkspaceRepository;

    public ProjectServiceImpl(ModelMapper modelMapper, ProjectRepository projectRepository, PersonalWorkspaceRepository personalWorkspaceRepository, OfficialWorkspaceRepository officialWorkspaceRepository) {
        this.modelMapper = modelMapper;
        this.projectRepository = projectRepository;
        this.personalWorkspaceRepository = personalWorkspaceRepository;
        this.officialWorkspaceRepository = officialWorkspaceRepository;
    }


    @Override
    public Project createProjectForPersonalWorkspace(ProjectRequest request, Long workspaceId) throws WorkspaceException, UserException, ProjectException {
        PersonalWorkspace workSpace = personalWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace does not exist"));
        List<Project> projects = workSpace.getProjects();


        if (isValidProject(projects, request.getName())) {
            Project project = modelMapper.map(request, Project.class);
            project.setReferenceName(generateReferenceName(request.getName()));
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

    private boolean isValidProject(List<Project> projects, String name) {
        return projects.stream().noneMatch(project -> project.getName().equalsIgnoreCase(name));
    }

    private String generateReferenceName(String projectName) {
        return projectName.substring(0, 2).toUpperCase();
    }
}
