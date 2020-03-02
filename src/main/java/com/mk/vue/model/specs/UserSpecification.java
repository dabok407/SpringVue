package com.mk.vue.model.specs;

import com.mk.vue.model.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserSpecification {

    public static Specification<User> searchWith(Map<String, Object> searchKeyword) {
        return (Specification<User>) ((root, query, builder) -> {
            List<Predicate> predicate = getPredicateWithKeyword(searchKeyword, root, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        });
    }

    private static List<Predicate> getPredicateWithKeyword(Map<String, Object> searchKeyword, Root<User> root, CriteriaBuilder builder) {
        List<Predicate> predicate = new ArrayList<>();
        for (String key : searchKeyword.keySet()) {
            predicate.add(builder.equal(root.get(key), searchKeyword.get(key)));
        }
        return predicate;
    }

    public Specification<User> accountEqual(String account) {
        if(account != null && !account.isEmpty()){
            return (Specification<User>) ((root, query, builder) ->
                    builder.equal(root.get("account"), account)
            );
        }else{
            return null;
        }
    }

    public Specification<User> roleEqual(String role) {
        if(role != null && !role.isEmpty()) {
            return (Specification<User>) ((root, query, builder) ->
                    builder.equal(root.get("role"), role)
            );
        }else{
            return null;
        }
    }
}