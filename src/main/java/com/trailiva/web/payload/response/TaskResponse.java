package com.trailiva.web.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private String name;

    private String priority;

    private LocalDateTime dueDate;

    private String description;
}
