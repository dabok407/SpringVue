package com.mk.vue.repository;

import com.mk.vue.model.entity.User;
import com.mk.vue.model.enumclass.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findFirstByPhoneNumberOrderByIdDesc(String phoneNumber);

    Page<User> findAllByAccount(Pageable pageable, String account);

    Page<User> findAllByStatus(Pageable pageable, UserStatus status);

    Page<User> findAllByAccountAndStatus(Pageable pageable, String account, UserStatus status);

    Page<User> findAll(Specification<User> spec, Pageable pageable);

    List<User> findByAccountContaining(String account);
}
