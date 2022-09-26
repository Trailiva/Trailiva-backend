package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OfficialWorkspace extends RepresentationModel<OfficialWorkspace> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long WorkspaceId;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(unique = true, nullable = false)
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
        this.projects.add(project);
    }

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "workspace_contributors",
            joinColumns = @JoinColumn(name="workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> contributors = new HashSet<>();


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "workspace_moderators",
            joinColumns = @JoinColumn(name="workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> moderators = new HashSet<>();

}
