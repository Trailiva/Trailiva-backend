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
    public void create(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException {
        User  user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        if(existByName(request.getName())){
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
    }

    private boolean existByName(String name) {
        return workspaceRepository.existsByName(name);
    }

    private WorkSpace saveWorkspace(WorkSpace workSpace) {
       return workspaceRepository.save(workSpace);
    }
}
