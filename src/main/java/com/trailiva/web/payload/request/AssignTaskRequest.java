package com.trailiva.web.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssignTaskRequest {
    private Long taskId;
    private Long contributorId;
    private Long moderatorId;
    private Long workspaceId;
}
