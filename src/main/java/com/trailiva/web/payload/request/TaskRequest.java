package com.trailiva.web.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trailiva.data.model.PriorityField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class TaskRequest {

    @NotBlank(message="task name cannot be blank")
    private String name;

    private String priority;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    private String description;
}