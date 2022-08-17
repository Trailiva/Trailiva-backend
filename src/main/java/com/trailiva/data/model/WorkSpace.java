package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class WorkSpace{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long WorkspaceId;

    private String name;
    private String description;
    private String referenceName;
    private String workSpaceType;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime datePublished;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDated;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Project> projects;

    public void addProject(Project project){
        projects.add(project);
    }
}
