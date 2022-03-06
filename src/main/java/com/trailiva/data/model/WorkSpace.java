package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class WorkSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workspaceId;
    private String name;
    private String description;
    private String referenceName;
    private WorkSpaceType workSpaceType;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDate datePublished;
    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDate updatedDated;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Task> tasks;

    @OneToMany(fetch = FetchType.EAGER)
    private List<TaskTab> tabs;
}
