package com.trailiva.service;

import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.data.model.User;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.data.model.WorkSpaceType;
import com.trailiva.data.repository.OfficialWorkspaceRepository;
import com.trailiva.data.repository.RoleRepository;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;
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
            createdWorkspace = createPersonalWorkspace(request, user);

        else if (WorkSpaceType.OFFICIAL == request.getWorkSpaceType())
            createdWorkspace = createOfficialWorkspace(request, user);

        return createdWorkspace;
    }

    @Override
    public WorkSpace addMemberToOfficialWorkspace(Long workspaceId, String memberEmail) throws UserException, WorkspaceException, BadRequestException {
        User user = userRepository.findByEmail(memberEmail).orElseThrow(() -> new UserException("User not found"));
        OfficialWorkspace  workspace = getOfficialWorkspace(workspaceId);
        user.getOfficialWorkspace().add(workspace);
        User savedUser = userRepository.save(user);
        workspace.getMembers().add(savedUser);
        workspaceRepository.save(workspace);
        return workspace;
    }


    private WorkSpace createPersonalWorkspace(WorkspaceRequest request, User user){
        WorkSpace workSpace = modelMapper.map(request, WorkSpace.class);
        WorkSpace space = saveWorkspace(workSpace);
        user.addWorkSpace(space);
        userRepository.save(user);
        return space;
    }

    private OfficialWorkspace createOfficialWorkspace(WorkspaceRequest request, User user) throws WorkspaceException, UserException {
        user.getRoles().add(roleRepository.findByName("ROLE_SUPER_MODERATOR").get());
        OfficialWorkspace workSpace = modelMapper.map(request, OfficialWorkspace.class);
        user.getOfficialWorkspace().add(workSpace);
        userRepository.save(user);
        OfficialWorkspace savedWorkspace = officialWorkspaceRepository.save(workSpace);
        savedWorkspace.getMembers().add(user);
        return savedWorkspace;
    }


    @Override
    public Set<WorkSpace> getWorkspaces(Long userId) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        return user.getWorkspaces();
    }

    @Override
    public WorkSpace getWorkspace(Long workspaceId) throws WorkspaceException {
        return workspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public OfficialWorkspace getOfficialWorkspace(Long workspaceId) throws WorkspaceException {
        return officialWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }


    private boolean existByName(String name) {
        return workspaceRepository.existsByName(name);
    }

    private WorkSpace saveWorkspace(WorkSpace workSpace) {
        return workspaceRepository.save(workSpace);
    }
}
