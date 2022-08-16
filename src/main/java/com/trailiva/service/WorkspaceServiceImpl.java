package com.trailiva.service;

import com.trailiva.data.model.*;
import com.trailiva.data.repository.OfficialWorkspaceRepository;
import com.trailiva.data.repository.RoleRepository;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.data.repository.PersonalWorkspaceRepository;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final ModelMapper modelMapper;
    private final PersonalWorkspaceRepository personalWorkspaceRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OfficialWorkspaceRepository officialWorkspaceRepository;


    @Override
    public WorkSpace createWorkspace(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        WorkSpace createdWorkspace = null;
        if (existByName(request.getName()))
            throw new WorkspaceException("Workspace with name already exist");

        if (WorkSpaceType.PERSONAL == request.getWorkSpaceType())
           createdWorkspace =  createPersonalWorkspace(request, user);

        else if (WorkSpaceType.OFFICIAL == request.getWorkSpaceType())
            createdWorkspace = createOfficialWorkspace(request, user);

        return createdWorkspace;
    }

    @Override
    public OfficialWorkspace addMemberToOfficialWorkspace(String memberEmail, Long userId) throws UserException, WorkspaceException, BadRequestException {
        User user = userRepository.findByEmail(memberEmail).orElseThrow(() -> new UserException("User not found"));
        OfficialWorkspace  workspace = getUserOfficialWorkspace(userId);
        workspace.getMembers().add(user);
        userRepository.save(user);
        return officialWorkspaceRepository.save(workspace);
    }

    @Override
    public WorkSpace addModeratorToOfficialWorkspace(String memberEmail, Long userId) throws UserException, WorkspaceException {
        User user = userRepository.findByEmail(memberEmail).orElseThrow(() -> new UserException("User not found"));
        user.getRoles().add(roleRepository.findByName("ROLE_MODERATOR").get());
        OfficialWorkspace  workspace = getUserOfficialWorkspace(userId);
        workspace.getMembers().add(user);
        userRepository.save(user);
        return officialWorkspaceRepository.save(workspace);
    }


    private WorkSpace createPersonalWorkspace(WorkspaceRequest request, User user){
       PersonalWorkspace workSpace = modelMapper.map(request, com.trailiva.data.model.PersonalWorkspace.class);
       PersonalWorkspace space = savePersonalWorkspace(workSpace);
        user.setPersonalWorkspace(space);
        userRepository.save(user);
        return space;
    }

    private WorkSpace createOfficialWorkspace(WorkspaceRequest request, User user) throws WorkspaceException, UserException {
        user.getRoles().add(roleRepository.findByName("ROLE_SUPER_MODERATOR").get());
        OfficialWorkspace workSpace = modelMapper.map(request, OfficialWorkspace.class);
        user.setOfficialWorkspace(workSpace);
        userRepository.save(user);
        return saveOfficialWorkspace(workSpace);
    }

    @Override
    public WorkSpace getUserPersonalWorkspace(Long userId) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        return user.getPersonalWorkspace();
    }

    public WorkSpace getPersonalWorkspace(Long workspaceId) throws WorkspaceException {
        return personalWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public OfficialWorkspace getOfficialWorkspace(Long workspaceId) throws WorkspaceException {
        return officialWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public OfficialWorkspace getUserOfficialWorkspace(Long userId) throws WorkspaceException {
        return officialWorkspaceRepository.findById(userId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }


    private boolean existByName(String name) {
        return personalWorkspaceRepository.existsByName(name);
    }

    private com.trailiva.data.model.PersonalWorkspace savePersonalWorkspace(com.trailiva.data.model.PersonalWorkspace workSpace) {
        return personalWorkspaceRepository.save(workSpace);
    }

    private OfficialWorkspace saveOfficialWorkspace(OfficialWorkspace workSpace) {
        return officialWorkspaceRepository.save(workSpace);
    }
}
