package com.trailiva.web.payload.request;

import com.trailiva.data.model.WorkSpaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceRequest {
    @NotBlank(message = "Workspace name cannot be blank")
    private String name;
    private String description;
    @NotBlank(message = "Workspace reference name cannot be blank")
    private String referenceName;

    @NotBlank(message = "Workspace must have a type")
    @Enumerated(EnumType.STRING)
    private WorkSpaceType workSpaceType;
}
