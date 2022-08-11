package com.trailiva.specification;

import com.trailiva.data.model.Task;
import com.trailiva.util.Helper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskSpecifications {

    public static Specification<Task> withTaskName(String taskName) {
        if (Helper.isNullOrEmpty(taskName))
            return null;
        final String wildcard = "%" + taskName + "%";
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), wildcard));
    }

    public static Specification<Task> withTaskDescription(String taskDesc) {
        if (Helper.isNullOrEmpty(taskDesc))
            return null;
        final String wildcard = "%" + taskDesc + "%";
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), wildcard));
    }
}
