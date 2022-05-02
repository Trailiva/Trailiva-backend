package com.trailiva.service;

import com.trailiva.data.model.User;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService{
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public WorkSpace createWorkspace(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException {
        User  user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        if(existByName(request.getName()) || existByReferenceName(request.getReferenceName())){
            throw new WorkspaceException("Workspace with name already exist");
        }
        WorkSpace workSpace = new WorkSpace();
        workSpace.setWorkSpaceType(request.getWorkSpaceType());
        workSpace.setName(request.getName());
        workSpace.setDescription(request.getDescription());
        workSpace.setReferenceName(request.getReferenceName());

        WorkSpace space = saveWorkspace(workSpace);
        log.info("Workspace data ==> {}", space);
        user.addWorkSpace(workSpace);
        userRepository.save(user);
        return space;
    }

    private boolean existByReferenceName(String referenceName) {
       return workspaceRepository.existsByReferenceName(referenceName);
    }

    @Override
    public List<WorkSpace> getWorkspaces(Long userId) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        return user.getWorkspaces();
    }

    @Override
    public WorkSpace getWorkspace(Long workspaceId) throws WorkspaceException {
       return workspaceRepository.findById(workspaceId).orElseThrow(
               ()-> new  WorkspaceException("Workspace not found"));
    }


    private boolean existByName(String name) {
        return workspaceRepository.existsByName(name);
    }

    private WorkSpace saveWorkspace(WorkSpace workSpace) {
       return workspaceRepository.save(workSpace);
    }
}
