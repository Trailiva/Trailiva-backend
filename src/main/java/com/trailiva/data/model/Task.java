package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String priority;

    private String description;

    private boolean isAssigned;

    private boolean isRequested;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dueDate;

    private boolean elapse = false;

    private String taskReference;

    private String tab;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;


}
