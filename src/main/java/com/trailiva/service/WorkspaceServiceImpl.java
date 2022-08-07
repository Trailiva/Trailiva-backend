package com.trailiva.service;

import com.trailiva.data.model.User;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.data.repository.WorkspaceRepository;
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

@Service
@Slf4j
@AllArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService{

    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WorkSpace createWorkspace(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException {
        User  user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        if(existByName(request.getName())){
            throw new WorkspaceException("Workspace with name already exist");
        }
        WorkSpace workSpace = modelMapper.map(request, WorkSpace.class);
        WorkSpace space = saveWorkspace(workSpace);
        user.addWorkSpace(space);
        userRepository.save(user);
        return space;
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
