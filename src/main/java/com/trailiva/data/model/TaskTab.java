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

    private Tab tab;

}
