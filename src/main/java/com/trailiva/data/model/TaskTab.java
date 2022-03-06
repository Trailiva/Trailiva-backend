package com.trailiva.data.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tabs")
public class TaskTab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tab;

    @OneToOne(mappedBy = "tab")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace")
    private WorkSpace workSpace;
}
