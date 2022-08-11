package com.trailiva.specification;

import com.trailiva.data.model.User;
import com.trailiva.util.Helper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSpecifications {
    public static Specification<User> withFirstName(String firstName){
        if (Helper.isNullOrEmpty(firstName))
            return null;
        final String wildcard = "%" + firstName + "%";
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), wildcard));
    }
}
