package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OfficialWorkspace extends WorkSpace{

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "workspace_members",
            joinColumns = @JoinColumn(name="workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "workspace_moderators",
            joinColumns = @JoinColumn(name="workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> moderators = new HashSet<>();
}
