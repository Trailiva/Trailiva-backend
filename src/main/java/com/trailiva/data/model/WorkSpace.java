package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

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
    private LocalDate datePublished;
    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDate updatedDated;
}
