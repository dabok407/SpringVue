package com.mk.vue.service;

import com.mk.vue.common.CommonFunction;
import com.mk.vue.common.CommonObjectUtils;
import com.mk.vue.ifs.CrudInterface;
import com.mk.vue.model.entity.OrderDetail;
import com.mk.vue.model.entity.OrderGroup;
import com.mk.vue.model.network.Header;
import com.mk.vue.model.network.Pagination;
import com.mk.vue.model.network.request.OrderDetailApiRequest;
import com.mk.vue.model.network.request.OrderGroupApiRequest;
import com.mk.vue.model.network.response.OrderDetailApiResponse;
import com.mk.vue.model.network.response.OrderGroupApiResponse;
import com.mk.vue.model.specs.OrderGroupSpecification;
import com.mk.vue.repository.ItemRepository;
import com.mk.vue.repository.OrderDetailRepository;
import com.mk.vue.repository.OrderGroupRepository;
import com.mk.vue.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderGroupApiLogicService implements CrudInterface<OrderGroupApiRequest, OrderGroupApiResponse> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ItemRepository itemRepository;


    @Override
    public Header<OrderGroupApiResponse> create(Header<OrderGroupApiRequest> request) {

        return Optional.ofNullable(request.getData())
                .map(body ->{
                    OrderGroup orderGroup = OrderGroup.builder()
                            .status(body.getStatus())
                            .orderType(body.getOrderType())
                            .revAddress(body.getRevAddress())
                            .revName(body.getRevName())
                            .paymentType(body.getPaymentType())
                            .totalPrice(body.getTotalPrice())
                            .totalQuantity(body.getTotalQuantity())
                            .orderAt(LocalDateTime.now())
                            .user(userRepository.getOne(body.getUserId()))
                            .build();

                    return orderGroup;
                })
                .map(newOrderGroup -> {
                    /*baseRepository.save(newOrderGroup);*/
                    orderGroupRepository.save(newOrderGroup);
                    for(OrderDetailApiRequest orderDetailList : request.getData().getOrderDetailApiRequestList()){
                        OrderDetail orderDetail = OrderDetail.builder()
                                .status(orderDetailList.getStatus())
                                .quantity(orderDetailList.getQuantity())
                                .totalPrice(orderDetailList.getTotalPrice())
                                .item(itemRepository.getOne(orderDetailList.getItemId()))
                                /*.arrivalDate(LocalDateTime.now())*/
                                .orderGroup(newOrderGroup)
                                .build();
                        orderDetailRepository.save(orderDetail);
                    }
                    return newOrderGroup;
                })
                //.map(newOrderGroup -> orderGroupRepository.save(newOrderGroup))
                .map(newOrderGroup -> response(newOrderGroup))
                .map(Header::OK)
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<OrderGroupApiResponse> read(Long id) {
        return orderGroupRepository.findById(id)
                .map(this::response)
                .map(Header::OK)
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<OrderGroupApiResponse> update(Header<OrderGroupApiRequest> request) {

        OrderGroupApiRequest body = request.getData();

        Optional<OrderGroup> optional = orderGroupRepository.findById(body.getId());

        return optional.map(orderGroup -> {
            orderGroup.setStatus(body.getStatus())
                    .setOrderType(body.getOrderType())
                    .setRevAddress(body.getRevAddress())
                    .setRevName(body.getRevName())
                    .setPaymentType(body.getPaymentType())
                    .setTotalPrice(body.getTotalPrice())
                    .setTotalQuantity(body.getTotalQuantity())
                    .setOrderAt(body.getOrderAt())
                    .setArrivalDate(body.getArrivalDate())
                    .setUser(userRepository.getOne(body.getUserId()))
            ;
            return orderGroup;
        })
        .map(orderGroup -> {
            orderGroupRepository.save(orderGroup);
            orderDetailRepository.deleteByOrderGroup(orderGroup);
            for(OrderDetailApiRequest orderDetailList : request.getData().getOrderDetailApiRequestList()){
                OrderDetail orderDetail = OrderDetail.builder()
                        .id(orderDetailList.getId())
                        .status(orderDetailList.getStatus())
                        .quantity(orderDetailList.getQuantity())
                        .totalPrice(orderDetailList.getTotalPrice())
                        .item(itemRepository.getOne(orderDetailList.getItemId()))
                        /*.arrivalDate(LocalDateTime.now())*/
                        .orderGroup(orderGroup)
                        .build();
                orderDetailRepository.save(orderDetail);
            }
            return orderGroup;
        })
        .map(orderGroup -> response(orderGroup))                        // userApiResponse
        .map(Header::OK)
        .orElseGet(()->Header.ERROR("데이터 없음"));


    }
    /*public Header<OrderGroupApiResponse> update(Header<OrderGroupApiRequest> request) {

        return Optional.ofNullable(request.getData())
                .map(body ->{
                    return orderGroupRepository.findById(body.getId());
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(orderGroup -> {
                    OrderGroupApiRequest body = request.getData();
                    orderGroup.setStatus(body.getStatus())
                            .setOrderType(body.getOrderType())
                            .setRevAddress(body.getRevAddress())
                            .setRevName(body.getRevName())
                            .setPaymentType(body.getPaymentType())
                            .setTotalPrice(body.getTotalPrice())
                            .setTotalQuantity(body.getTotalQuantity())
                            .setOrderAt(body.getOrderAt())
                            .setArrivalDate(body.getArrivalDate())
                            .setUser(userRepository.getOne(body.getUserId()))
                    ;
                    return orderGroup;
                })
                .map(orderGroup -> {
                    orderGroupRepository.save(orderGroup);
                    for(OrderDetailApiRequest orderDetailList : request.getData().getOrderDetailApiRequestList()){
                        OrderDetail orderDetail = OrderDetail.builder()
                                .id(orderDetailList.getId())
                                .status(orderDetailList.getStatus())
                                .quantity(orderDetailList.getQuantity())
                                .totalPrice(orderDetailList.getTotalPrice())
                                .item(itemRepository.getOne(orderDetailList.getItemId()))
                                .arrivalDate(LocalDateTime.now())
                                .orderGroup(orderGroup)
                                .build();
                        orderDetailRepository.save(orderDetail);
                    }
                    return orderGroup;
                })
                *//*.map(changeOrderGroup -> orderGroupRepository.save(changeOrderGroup))*//*
                .map(this::response)
                .map(Header::OK)
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }*/

    @Override
    public Header delete(Long id) {

        return orderGroupRepository.findById(id)
                .map(orderGroup -> {
                    orderGroupRepository.delete(orderGroup);
                    return Header.OK();
                    /*return orderGroup;*/
                })
                /*.map(orderGroup -> {
                    orderDetailRepository.deleteByOrderGroupId(orderGroup.getId());
                    return Header.OK();
                })*/
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    public Header<List<OrderGroupApiResponse>> search(Pageable pageable, OrderGroupApiRequest orderGroupApiRequest, String initialYn){

        Map<String, Object> searchRequest = CommonObjectUtils.convertObjectToMap(orderGroupApiRequest);
        Map<String, Object> searchKeys = new HashMap<>();

        for (String key : searchRequest.keySet()) {
            String value = String.valueOf(searchRequest.get(key));
            if(value != null && !value.isEmpty() && !"null".equals(value)){
                searchKeys.put(key, searchRequest.get(key));
            }
        }

        Page<OrderGroup> orderGroups =  searchKeys.isEmpty() ?
                orderGroupRepository.findAll(pageable) :
                orderGroupRepository.findAll(OrderGroupSpecification.searchWith(searchKeys), pageable);

        /*Page<OrderGroup> orderGroups = orderGroupRepository.findAll(pageable);*/

        List<OrderGroupApiResponse> orderGroupApiResponseList = orderGroups.stream()
                .map(orderGroup -> response(orderGroup)
                    /*
                    {
                    OrderGroupApiResponse orderGroupApiResponse = this.response(orderGroup);
                    List<OrderDetailApiResponse> orderDetails = orderGroup.getOrderDetailList().stream()
                            .map(detail -> this.response(detail))
                            .collect(Collectors.toList());

                    orderGroupApiResponse.setOrderDetailList(orderDetails);
                    return orderGroupApiResponse;
                }*/
                )
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(orderGroups.getTotalPages())
                .totalElements(orderGroups.getTotalElements())
                .currentPage(orderGroups.getNumber())
                .currentElements(orderGroups.getNumberOfElements())
                .build();

        return Header.OK(orderGroupApiResponseList, pagination);
    }

    public OrderGroupApiResponse response(OrderGroup orderGroup){

        OrderGroupApiResponse orderGroupApiResponse = OrderGroupApiResponse.builder()
                .id(orderGroup.getId())
                .status(orderGroup.getStatus())
                .orderType(orderGroup.getOrderType())
                .revAddress(orderGroup.getRevAddress())
                .revName(orderGroup.getRevName())
                .paymentType(orderGroup.getPaymentType())
                .totalPrice(CommonFunction.convertCommaMoney(String.valueOf(orderGroup.getTotalPrice())))
                .totalQuantity(orderGroup.getTotalQuantity())
                .orderAt(orderGroup.getOrderAt())
                .arrivalDate(orderGroup.getArrivalDate())
                .userId(orderGroup.getUser().getId())
                .userAccount(orderGroup.getUser().getAccount())
                .orderDetailList(response(orderGroup.getOrderDetailList()))
                /*.castOrderDetailList(orderGroup.getOrderDetailList()) */
                .build();

        return orderGroupApiResponse;
    }

    public OrderDetailApiResponse response(OrderDetail orderDetail){

        OrderDetailApiResponse orderDetailApiResponse = OrderDetailApiResponse.builder()
                .id(orderDetail.getId())
                .status(orderDetail.getStatus())
                .totalPrice(orderDetail.getTotalPrice())
                .quantity(orderDetail.getQuantity())
                .arrivalDate(orderDetail.getArrivalDate())
                .build();

        return orderDetailApiResponse;
    }

    public List<OrderDetailApiResponse> response(List<OrderDetail> orderDetailList){
        List<OrderDetailApiResponse> returnList = new ArrayList<>();
        for (OrderDetail list : orderDetailList){
            OrderDetailApiResponse orderDetailApiResponse = OrderDetailApiResponse.builder()
                    .id(list.getId())
                    .status(list.getStatus())
                    .totalPrice(list.getTotalPrice())
                    .quantity(list.getQuantity())
                    .name(list.getItem().getName())
                    .itemId(list.getItem().getId())
                    .arrivalDate(list.getArrivalDate())
                    .build();
            returnList.add(orderDetailApiResponse);
        }
        return returnList;
    }

}
