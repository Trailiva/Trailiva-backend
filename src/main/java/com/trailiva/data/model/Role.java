package com.trailiva.data.model;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long roleId;

  @NaturalId
  @Column(length = 60)
  private String name;

  @ManyToMany(mappedBy = "roles")
  private Collection<User> users;

  public Role(String name) {
    this.name = name;
  }
}
