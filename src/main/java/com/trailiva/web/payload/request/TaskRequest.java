package com.trailiva.web.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message="task name cannot be blank")
    private String name;

    private String priority;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dueDate;

    private String description;

}
