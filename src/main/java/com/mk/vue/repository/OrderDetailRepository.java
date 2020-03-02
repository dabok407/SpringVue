package com.mk.vue.repository;

import com.mk.vue.model.entity.OrderDetail;
import com.mk.vue.model.entity.OrderGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {

    List<OrderDetail> findByOrderGroup_Id(Long orderGroupId);

    @Transactional
    int deleteByOrderGroup(OrderGroup orderGroup);


    int deleteByOrderGroupId(Long orderGroupId);
}
