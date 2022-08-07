package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkSpace extends RepresentationModel<WorkSpace> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workspaceId;

    private String name;
    private String description;
    private String referenceName;
    private WorkSpaceType workSpaceType;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime datePublished;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDated;

    @OneToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Project> projects;

    public void addProject(Project project){
        projects.add(project);
    }
}
