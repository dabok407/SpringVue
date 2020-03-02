package com.mk.vue.model.specs;

import com.mk.vue.common.CommonObjectUtils;
import com.mk.vue.model.entity.*;
import com.mk.vue.model.entity.OrderGroup;
import com.mk.vue.model.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OrderGroupSpecification {

    public static Specification<OrderGroup> searchWith(Map<String, Object> searchKeyword) {
        return (Specification<OrderGroup>) ((root, query, builder) -> {
            List<Predicate> predicate = getPredicateWithKeyword(searchKeyword, root, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        });
    }

    /*public static Specification<Client> clientsOfGroup(long groupNo) {
        return (root, query, cb) -> {
            Join<Client, ClientGroup> join = root.join(Client_.groups);
            return cb.equal(join.get(ClientGroup_.group).get(Group_.no), groupNo);
        };
    }*/

    private static List<Predicate> getPredicateWithKeyword(Map<String, Object> searchKeyword, Root<OrderGroup> root, CriteriaBuilder builder) {
        List<Predicate> predicate = new ArrayList<>();
        for (String key : searchKeyword.keySet()) {
            if("brandName".equals(key)){
                predicate.add(builder.like(root.get(key), "%"+searchKeyword.get(key)+"%"));
            }else if("user".equals(key)){
                Join<OrderGroup, User> join = root.join("user");
                Map<String, Object> UserKeyword = CommonObjectUtils.convertObjectToMap(searchKeyword.get(key));
                for (String userKey : UserKeyword.keySet()) {
                    if("account".equals(userKey)){
                        predicate.add(builder.like(join.get("account"), "%"+ UserKeyword.get("account")+"%"));
                    }
                    /*else if("phoneNumber".equals(userKey)){
                        predicate.add(builder.like(join.get("phoneNumber"), "%"+ UserKeyword.get("phoneNumber")+"%"));
                    }*/
                }
            }else{
                predicate.add(builder.equal(root.get(key), searchKeyword.get(key)));
            }
        }
        return predicate;
    }

}