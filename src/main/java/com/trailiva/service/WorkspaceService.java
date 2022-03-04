package com.trailiva.service;

import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

public interface WorkspaceService {
    void create(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException;
}
