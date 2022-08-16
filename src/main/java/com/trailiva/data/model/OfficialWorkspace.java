package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfficialWorkspace extends WorkSpace{

    @JsonIgnore
    @ManyToMany()
    @JoinTable(
            name = "member_workspace",
            joinColumns = @JoinColumn(name="workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();


    @JsonIgnore
    @ManyToMany()
    @JoinTable(
            name = "moderator_workspace",
            joinColumns = @JoinColumn(name="workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> moderator = new HashSet<>();

}
