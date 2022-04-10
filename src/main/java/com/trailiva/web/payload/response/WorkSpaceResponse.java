package com.trailiva.web.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trailiva.data.model.Project;
import com.trailiva.data.model.TaskTab;
import com.trailiva.data.model.WorkSpaceType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;

@Data
public class WorkSpaceResponse {
    private String name;
    private String description;
    private String referenceName;
    private WorkSpaceType workSpaceType;
    private List<Project> projects;
    private List<TaskTab> tabs;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDate datePublished;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDate updatedDated;
}
