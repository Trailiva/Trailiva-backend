package com.trailiva.specification;

import com.trailiva.data.model.WorkSpace;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkspaceSpecifications {

    public static Specification<WorkSpace> withWorkspaceId(Long id){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("workspaceId"), id));
    }
}
