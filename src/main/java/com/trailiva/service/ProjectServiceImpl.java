package com.trailiva.service;

import com.trailiva.data.model.Project;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.data.repository.ProjectRepository;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.ProjectRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;

    public ProjectServiceImpl(ProjectRepository workspaceRepository, ModelMapper modelMapper, ProjectRepository projectRepository, WorkspaceRepository workspaceRepository1) {
        this.modelMapper = modelMapper;
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository1;
    }


    @Override
    public Project createProject(ProjectRequest request, Long workspaceId) throws WorkspaceException, UserException, ProjectException {
        WorkSpace workSpace = workspaceRepository.findById(workspaceId).orElseThrow(
                ()-> new WorkspaceException("Workspace does not exist"));
        List<Project> projects = workSpace.getProjects();


        if(!isValidProject(projects, request.getName())){
            Project project = modelMapper.map(request, Project.class);
            Project savedProject = projectRepository.save(project);
            workSpace.addProject(savedProject);
            workspaceRepository.save(workSpace);
            return savedProject;
        }else throw new ProjectException("Project name already exist on your project list");
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

    private boolean isValidProject(List<Project> projects, String name) {
       return projects.stream().anyMatch(project -> project.getName().equalsIgnoreCase(name));
    }
}
