package com.mk.vue.service;

import com.mk.vue.common.CommonObjectUtils;
import com.mk.vue.common.ShaPasswordEncoder;
import com.mk.vue.ifs.CrudInterface;
import com.mk.vue.model.entity.AdminUser;
import com.mk.vue.model.enumclass.AdminUserStatus;
import com.mk.vue.model.enumclass.LoginFailType;
import com.mk.vue.model.network.Header;
import com.mk.vue.model.network.Pagination;
import com.mk.vue.model.network.request.AdminUserApiRequest;
import com.mk.vue.model.network.response.*;
import com.mk.vue.model.network.response.AdminUserApiResponse;
import com.mk.vue.model.specs.AdminUserSpecification;
import com.mk.vue.repository.AdminUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AdminUserApiLogicService implements CrudInterface<AdminUserApiRequest, AdminUserApiResponse> {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private AdminUserSpecification adminUserSpecification;


    // 1. request data
    // 2. user 생성
    // 3. 생성된 데이터 -> UserApiResponse return
    @Override
    public Header<AdminUserApiResponse> create(Header<AdminUserApiRequest> request) {

        // 1. request data
        AdminUserApiRequest adminUserApiRequest = request.getData();

        // 2. 중복검사
        boolean adminUserValidation = verifyDuplicateAccount(adminUserApiRequest.getAccount());
        if(!adminUserValidation){
            return Header.ERROR(LoginFailType.NONE_ACCOUNT.getMsg());
        }
        // 3. User 생성
        AdminUser adminUser = AdminUser.builder()
                .account(adminUserApiRequest.getAccount())
                .password(adminUserApiRequest.getPassword())
                .status(AdminUserStatus.REGISTERED)
                .role(adminUserApiRequest.getRole())
                .loginFailCount(0)
                .registeredAt(LocalDateTime.now())
                .build();
        AdminUser newAdminUser = adminUserRepository.save(adminUser);

        // 4. 생성된 데이터 -> userApiResponse return
        return Header.OK(response(newAdminUser));
    }

    @Override
    public Header<AdminUserApiResponse> read(Long id) {

        return adminUserRepository.findById(id)
            .map(adminUser -> response(adminUser))
            .map(Header::OK)
            .orElseGet(
                    ()->Header.ERROR("데이터 없음")
            );
    }

    @Override
    public Header<AdminUserApiResponse> update(Header<AdminUserApiRequest> request) {
        // 1. data
        AdminUserApiRequest adminUserApiRequest = request.getData();

        // 2. id -> user 데이터 를 찾고
        Optional<AdminUser> optional = adminUserRepository.findById(adminUserApiRequest.getId());

        return optional.map(adminUser -> {
            // 3. data -> update
            // id
            BeanUtils.copyProperties(adminUserApiRequest, adminUser
                    , "password","status","loginFailCount","registeredAt", "unregisteredAt"
                    ,"createdAt","createdBy","updatedAt","updatedBy");
            if(!adminUserApiRequest.getPassword().isEmpty()){
                adminUser.setPassword(ShaPasswordEncoder.getSHA256(adminUserApiRequest.getPassword()));
                adminUser.setPasswordUpdatedAt(LocalDateTime.now());
            }
            adminUser.setUpdatedAt(LocalDateTime.now());
            /*adminUser.setAccount(adminUserApiRequest.getAccount())
                    .setPassword(adminUserApiRequest.getPassword())
                    .setStatus(adminUserApiRequest.getStatus())
                    .setRole(adminUserApiRequest.getRole())
                    .setRegisteredAt(adminUserApiRequest.getRegisteredAt())
                    .setUnregisteredAt(adminUserApiRequest.getUnregisteredAt())
                    ;*/
            return adminUser;

        })
        .map(adminUser -> adminUserRepository.save(adminUser))             // update -> newUser
        .map(adminUser -> response(adminUser))                        // userApiResponse
        .map(Header::OK)
        .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        // 1. id -> repository -> user
        Optional<AdminUser> optional = adminUserRepository.findById(id);

        // 2. repository -> delete
        return optional.map(adminUser ->{
            adminUserRepository.delete(adminUser);
            return Header.OK();
        })
        .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    public Header<List<AdminUserApiResponse>> search(Pageable pageable, AdminUserApiRequest adminUserApiRequest) {

        /*첫 번째*/
        Map<String, Object> searchRequest = CommonObjectUtils.convertObjectToMap(adminUserApiRequest);
        Map<String, Object> searchKeys = new HashMap<>();

        for (String key : searchRequest.keySet()) {
            String value = String.valueOf(searchRequest.get(key));
            if(value != null && !value.isEmpty() && !"null".equals(value)){
                searchKeys.put(key, searchRequest.get(key));
            }
        }

        Page<AdminUser> adminUsers =  searchKeys.isEmpty() ?
                    adminUserRepository.findAll(pageable) :
                    adminUserRepository.findAll(adminUserSpecification.searchWith(searchKeys), pageable);

        /* 두 번째
        Page<AdminUser> adminUsers = adminUserRepository.findAll
                                        (Specification.where
                                                (adminUserSpecification.accountEqual(adminUserApiRequest.getAccount()))
                                                .and(adminUserSpecification.roleEqual(adminUserApiRequest.getRole()))
                                        ,pageable);
        */

        /* 세 번째
        Page<AdminUser> adminUsers = null;
        if(adminUserApiRequest.getAccount() == null && adminUserApiRequest.getRole() == null){
            adminUsers = adminUserRepository.findAll(pageable);
        }else if(adminUserApiRequest.getAccount() != null && adminUserApiRequest.getRole() == null){
            adminUsers = adminUserRepository.findAllByAccount(pageable, adminUserApiRequest.getAccount());
        }else if(adminUserApiRequest.getRole() != null && adminUserApiRequest.getAccount() == null){
            adminUsers = adminUserRepository.findAllByRole(pageable, adminUserApiRequest.getRole());
        }else{
            adminUsers = adminUserRepository.findAllByAccountAndRole(pageable, adminUserApiRequest.getAccount(), adminUserApiRequest.getRole());
        }
        */

        List<AdminUserApiResponse> adminUserApiResponseList = adminUsers.stream()
                .map(adminUser -> response(adminUser))
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(adminUsers.getTotalPages())
                .totalElements(adminUsers.getTotalElements())
                .currentPage(adminUsers.getNumber())
                .currentElements(adminUsers.getNumberOfElements())
                .build();

        return Header.OK(adminUserApiResponseList,pagination);
    }

    private AdminUserApiResponse response(AdminUser adminUser){
        // user -> userApiResponse

        AdminUserApiResponse adminUserApiResponse = AdminUserApiResponse.builder()
                .id(adminUser.getId())
                .account(adminUser.getAccount())
                .password(adminUser.getPassword()) // todo 암호화, 길이
                .status(adminUser.getStatus())
                .role(adminUser.getRole())
                .registeredAt(adminUser.getRegisteredAt())
                .unregisteredAt(adminUser.getUnregisteredAt())
                .lastLoginAt(adminUser.getLastLoginAt())
                .loginFailCount(adminUser.getLoginFailCount())
                .build();

        return adminUserApiResponse;
    }

    private boolean verifyDuplicateAccount(String account){
        if(adminUserRepository.findByAccount(account).isPresent()){
            return false;
            /*throw new ValidCustomException("이미 사용중인 계정 입니다", "account");*/
        }
        return true;
    }

}
