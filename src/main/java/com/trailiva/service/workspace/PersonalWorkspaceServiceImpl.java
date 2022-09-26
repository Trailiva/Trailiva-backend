package com.trailiva.service.workspace;

import com.trailiva.data.model.PersonalWorkspace;
import com.trailiva.data.model.User;
import com.trailiva.data.repository.*;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalWorkspaceServiceImpl implements PersonalWorkspaceService {

    private final ModelMapper modelMapper;
    private final PersonalWorkspaceRepository personalWorkspaceRepository;
    private final UserRepository userRepository;


    public PersonalWorkspaceServiceImpl(
            ModelMapper modelMapper,
            PersonalWorkspaceRepository personalWorkspaceRepository,
            UserRepository userRepository) {

        this.modelMapper = modelMapper;
        this.personalWorkspaceRepository = personalWorkspaceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PersonalWorkspace createPersonalWorkspace(WorkspaceRequest request, Long userId) throws UserException, WorkspaceException {
        User user = getAUserById(userId);
        if (existByName(request.getName()))
            throw new WorkspaceException("Workspace with name already exist");
        PersonalWorkspace workSpace = modelMapper.map(request, PersonalWorkspace.class);
        PersonalWorkspace saveWorkspace = savePersonalWorkspace(workSpace);
        user.setPersonalWorkspace(saveWorkspace);
        userRepository.save(user);
        return saveWorkspace;
    }

    @Override
    public PersonalWorkspace getUserWorkspace(Long userId) throws UserException, WorkspaceException {
        User creator = getAUserById(userId);
        return Optional.of(creator.getPersonalWorkspace())
                .orElseThrow(() -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public PersonalWorkspace getWorkspace(Long workspaceId) throws WorkspaceException {
        return personalWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public int countWorkspaceProjects(Long workspaceId) throws WorkspaceException {
        PersonalWorkspace workspace = getWorkspace(workspaceId);
        return workspace.getProjects().size();
    }

    @Override
    public List<PersonalWorkspace> getAllWorkspace() {
        return personalWorkspaceRepository.findAll();
    }

    private boolean existByName(String name) {
        return personalWorkspaceRepository.existsByName(name);
    }

    private User getAUserById(Long id) throws UserException {
        return userRepository.findById(id).orElseThrow(() -> new UserException("User not found"));
    }

    private PersonalWorkspace savePersonalWorkspace(PersonalWorkspace workSpace) {
        return personalWorkspaceRepository.save(workSpace);
    }

}
