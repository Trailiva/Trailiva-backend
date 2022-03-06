package com.trailiva.data.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class PriorityField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Priority priority;

    @OneToOne(mappedBy = "priorityField")
    private Task task;

    public PriorityField(Priority priority) {
        this.priority = priority;
    }
}
