package com.mk.vue.service;

import com.mk.vue.common.CommonObjectUtils;
import com.mk.vue.ifs.CrudInterface;
import com.mk.vue.model.entity.OrderGroup;
import com.mk.vue.model.entity.User;
import com.mk.vue.model.enumclass.UserStatus;
import com.mk.vue.model.network.Header;
import com.mk.vue.model.network.Pagination;
import com.mk.vue.model.network.request.UserApiRequest;
import com.mk.vue.model.network.response.ItemApiResponse;
import com.mk.vue.model.network.response.OrderGroupApiResponse;
import com.mk.vue.model.network.response.UserApiResponse;
import com.mk.vue.model.network.response.UserOrderInfoApiResponse;
import com.mk.vue.model.specs.UserSpecification;
import com.mk.vue.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserApiLogicService implements CrudInterface<UserApiRequest , UserApiResponse> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderGroupApiLogicService orderGroupApiLogicService;

    @Autowired
    private ItemApiLogicService itemApiLogicService;

    @Autowired
    private UserSpecification userSpecification;


    // 1. request data
    // 2. user 생성
    // 3. 생성된 데이터 -> UserApiResponse return
    @Override
    public Header<UserApiResponse> create(Header<UserApiRequest> request) {

        // 1. request data
        UserApiRequest userApiRequest = request.getData();

        // 2. User 생성
        User user = User.builder()
                .account(userApiRequest.getAccount())
                .password(userApiRequest.getPassword())
                .status(UserStatus.REGISTERED)
                .phoneNumber(userApiRequest.getPhoneNumber())
                .email(userApiRequest.getEmail())
                .registeredAt(LocalDateTime.now())
                .build();
        User newUser = userRepository.save(user);

        // 3. 생성된 데이터 -> userApiResponse return
        return Header.OK(response(newUser));
    }

    @Override
    public Header<UserApiResponse> read(Long id) {

        return userRepository.findById(id)
            .map(user -> response(user))
            .map(Header::OK)
            .orElseGet(
                    ()->Header.ERROR("데이터 없음")
            );
    }

    @Override
    public Header<UserApiResponse> update(Header<UserApiRequest> request) {
        // 1. data
        UserApiRequest userApiRequest = request.getData();

        // 2. id -> user 데이터 를 찾고
        Optional<User> optional = userRepository.findById(userApiRequest.getId());

        return optional.map(user -> {
            // 3. data -> update
            // id
            user.setAccount(userApiRequest.getAccount())
                    .setPassword(userApiRequest.getPassword())
                    .setStatus(userApiRequest.getStatus())
                    .setPhoneNumber(userApiRequest.getPhoneNumber())
                    .setEmail(userApiRequest.getEmail())
                    .setUpdatedAt(LocalDateTime.now())
                    //.setRegisteredAt(userApiRequest.getRegisteredAt())
                    //.setUnregisteredAt(userApiRequest.getUnregisteredAt())
                    ;
            return user;

        })
        .map(user -> userRepository.save(user))             // update -> newUser
        .map(user -> response(user))                        // userApiResponse
        .map(Header::OK)
        .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        // 1. id -> repository -> user
        Optional<User> optional = userRepository.findById(id);

        // 2. repository -> delete
        return optional.map(user ->{
            userRepository.delete(user);
            return Header.OK();
        })
        .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    public Header<List<UserApiResponse>> search(Pageable pageable, UserApiRequest userApiRequest) {

        Map<String, Object> searchRequest = CommonObjectUtils.convertObjectToMap(userApiRequest);
        Map<String, Object> searchKeys = new HashMap<>();

        for (String key : searchRequest.keySet()) {
            String value = String.valueOf(searchRequest.get(key));
            if(value != null && !value.isEmpty() && !"null".equals(value)){
                searchKeys.put(key, searchRequest.get(key));
            }
        }

        Page<User> users = searchKeys.isEmpty() ?
                userRepository.findAll(pageable) :
                userRepository.findAll(userSpecification.searchWith(searchKeys), pageable);

        List<UserApiResponse> userApiResponseList = users.stream()
                .map(user -> response(user))
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .currentPage(users.getNumber())
                .currentElements(users.getNumberOfElements())
                .build();

        return Header.OK(userApiResponseList,pagination);
    }

    public Header<List<UserApiResponse>> search(String account) {

        List<User> users = userRepository.findByAccountContaining(account);

        List<UserApiResponse> userApiResponseList = users.stream()
                .map(user -> response(user))
                .collect(Collectors.toList());

        return Header.OK(userApiResponseList);
    }

    private UserApiResponse response(User user){
        // user -> userApiResponse

        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .account(user.getAccount())
                .password(user.getPassword()) // todo 암호화, 길이
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .registeredAt(user.getRegisteredAt())
                .unregisteredAt(user.getUnregisteredAt())
                .build();

        return userApiResponse;
    }

    public Header<UserOrderInfoApiResponse> orderInfo(Long id) {

        // user
        User user = userRepository.getOne(id);
        UserApiResponse userApiResponse = response(user);


        // orderGorup
        List<OrderGroup> orderGroupList = user.getOrderGroupList();
        List<OrderGroupApiResponse> orderGroupApiResponseList = orderGroupList.stream()
                .map(orderGroup -> {
                    OrderGroupApiResponse orderGroupApiResponse = orderGroupApiLogicService.response(orderGroup);

                    // item api response
                    List<ItemApiResponse> itemApiResponseList = orderGroup.getOrderDetailList().stream()
                            .map(detail -> detail.getItem())
                            .map(item -> itemApiLogicService.response(item))
                            .collect(Collectors.toList());

                    orderGroupApiResponse.setItemApiResponseList(itemApiResponseList);
                    return orderGroupApiResponse;
                })
                .collect(Collectors.toList());

        userApiResponse.setOrderGroupApiResponseList(orderGroupApiResponseList);
        UserOrderInfoApiResponse userOrderInfoApiResponse = UserOrderInfoApiResponse.builder()
                .userApiResponse(userApiResponse)
                .build();


        return Header.OK(userOrderInfoApiResponse);
    }
}
