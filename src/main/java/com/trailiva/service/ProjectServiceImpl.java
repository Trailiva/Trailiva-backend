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

    private final WorkspaceRepository workspaceRepository;
    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(WorkspaceRepository workspaceRepository, ModelMapper modelMapper, ProjectRepository projectRepository) {
        this.workspaceRepository = workspaceRepository;
        this.modelMapper = modelMapper;
        this.projectRepository = projectRepository;
    }


    @Override
    public void createProject(ProjectRequest request, Long workspaceId) throws WorkspaceException, UserException, ProjectException {
        WorkSpace workSpace = workspaceRepository.findById(workspaceId).orElseThrow(
                ()-> new WorkspaceException("Workspace does not exist"));
        List<Project> projects = workSpace.getProjects();

        if(!isValidProject(projects, request.getName())){
            Project project = modelMapper.map(request, Project.class);
            Project savedProject = projectRepository.save(project);
            workSpace.addProject(savedProject);
            workspaceRepository.save(workSpace);
        }else throw new ProjectException("Project name " + request.getName() + "Already exist on your project list");
    }

    @Override
    public void updateProject(ProjectRequest request, Long projectId) {

    }

    @Override
    public void deleteProject(Long projectId) {

    }

    private boolean isValidProject(List<Project> projects, String name) {
       return projects.stream().anyMatch(project -> project.getName().equalsIgnoreCase(name));
    }
}
