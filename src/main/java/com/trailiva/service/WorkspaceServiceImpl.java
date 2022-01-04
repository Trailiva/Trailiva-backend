package com.trailiva.service;

import com.trailiva.data.model.WorkSpace;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.web.payload.request.WorkspaceRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService{
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Override
    public void create(WorkspaceRequest request) {
        WorkSpace workSpace = modelMapper.map(request, WorkSpace.class);
        saveWorkspace(workSpace);
    }

    private WorkSpace saveWorkspace(WorkSpace workSpace) {
       return workspaceRepository.save(workSpace);
    }
}
