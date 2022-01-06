package com.trailiva.service;

import com.trailiva.web.payload.request.WorkspaceRequest;
import org.springframework.stereotype.Service;

public interface WorkspaceService {
    void create(WorkspaceRequest request);
}
