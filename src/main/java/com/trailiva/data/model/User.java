package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "user_data")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class User extends RepresentationModel<User> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long userId;
    private String firstName;
    private String lastName;
    private boolean isEnabled;

    @Column(unique = true)
    private String email;

    private String phoneNumber;
    @JsonIgnore
    private String password;

    private String imageUrl;
    private String publicId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private List<Role> roles = new ArrayList<>();

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime datePublished;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDated;

    @ManyToOne
    @JoinColumn(name = "personal_workspace_id")
    private PersonalWorkspace personalWorkspace;

    @ManyToOne
    @JoinColumn(name = "official_workspace_id")
    private OfficialWorkspace officialWorkspace;
}
