package com.mk.vue.repository;

import com.mk.vue.model.entity.OrderGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long> {

    Page<OrderGroup> findAll(Specification<OrderGroup> spec, Pageable pageable);
}
