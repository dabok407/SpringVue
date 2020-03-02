package com.mk.vue.service;

import com.mk.vue.common.CommonFunction;
import com.mk.vue.common.CommonObjectUtils;
import com.mk.vue.model.entity.Item;
import com.mk.vue.model.network.Header;
import com.mk.vue.model.network.Pagination;
import com.mk.vue.model.network.request.ItemApiRequest;
import com.mk.vue.model.network.response.ItemApiResponse;
import com.mk.vue.model.specs.ItemSpecification;
import com.mk.vue.repository.ItemRepository;
import com.mk.vue.repository.PartnerRepository;
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
public class ItemApiLogicService extends BaseService<ItemApiRequest, ItemApiResponse, Item> {

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerApiLogicService partnerApiLogicService;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Header<ItemApiResponse> create(Header<ItemApiRequest> request) {

        return Optional.ofNullable(request.getData())
            .map(body ->{
                Item item = Item.builder()
                    .status(body.getStatus())
                    .name(body.getName())
                    .title(body.getTitle())
                    .content(body.getContent())
                    .price(body.getPrice())
                    .brandName(body.getBrandName())
                    .registeredAt(LocalDateTime.now())
                    .partner(partnerRepository.getOne(body.getPartnerId()))
                    .build();

                return item;
            })
            .map(newItem -> baseRepository.save(newItem))
            .map(newItem -> response(newItem))
            .map(Header::OK)
            .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<ItemApiResponse> read(Long id) {

        return itemRepository.findById(id)
                .map(item -> response(item))
                .map(Header::OK)
                .orElseGet(()-> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<ItemApiResponse> update(Header<ItemApiRequest> request) {

        return Optional.ofNullable(request.getData())
                .map(body ->{
                    return itemRepository.findById(body.getId());
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(item -> {

                    ItemApiRequest body = request.getData();
                    item.setStatus(body.getStatus())
                            .setTitle(body.getTitle())
                            .setContent(body.getContent())
                            .setName(body.getName())
                            .setPrice(body.getPrice())
                            .setBrandName(body.getBrandName())
                            .setPartner(partnerRepository.getOne(body.getPartnerId()))
                            .setStatus(body.getStatus())
                            /*.setRegisteredAt(body.getRegisteredAt())
                            .setUnregisteredAt(body.getUnregisteredAt())*/
                            ;
                    return item;

                })
                .map(changeItem -> baseRepository.save(changeItem))
                .map(newItem -> response(newItem))
                .map(Header::OK)
                .orElseGet(()->Header.ERROR("데이터 없음"));

    }

    @Override
    public Header delete(Long id) {

        return baseRepository.findById(id)
            .map(item -> {
                baseRepository.delete(item);
                return Header.OK();
            })
            .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    public ItemApiResponse response(Item item){

        ItemApiResponse body = ItemApiResponse.builder()
                .id(item.getId())
                .status(item.getStatus())
                .name(item.getName())
                .title(item.getTitle())
                .content(item.getContent())
                .price(CommonFunction.convertCommaMoney(String.valueOf(item.getPrice())))
                .brandName(item.getBrandName())
                .registeredAt(item.getRegisteredAt())
                .unregisteredAt(item.getUnregisteredAt())
                .partnerId(item.getPartner().getId())
                .partner(partnerApiLogicService.read(item.getPartner().getId()).getData())
                .build();

        return body;
    }

    public Header<List<ItemApiResponse>> search(Pageable pageable, ItemApiRequest itemApiRequest, String initialYn) {

        /*첫 번째*/
        Map<String, Object> searchRequest = CommonObjectUtils.convertObjectToMap(itemApiRequest);
        Map<String, Object> searchKeys = new HashMap<>();

        for (String key : searchRequest.keySet()) {
            String value = String.valueOf(searchRequest.get(key));
            if(value != null && !value.isEmpty() && !"null".equals(value)){
                searchKeys.put(key, searchRequest.get(key));
            }
        }

        Page<Item> items =  searchKeys.isEmpty() ?
                itemRepository.findAll(pageable) :
                itemRepository.findAll(ItemSpecification.searchWith(searchKeys), pageable);


        List<ItemApiResponse> itemApiResponseList = items.stream()
                .map(item -> response(item))
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(items.getTotalPages())
                .totalElements(items.getTotalElements())
                .currentPage(items.getNumber())
                .currentElements(items.getNumberOfElements())
                .build();

        // init 데이터가 필요 할 때
        if("Y".equals(initialYn)){
            Map<String, Object> initMap = new HashMap<String, Object>();
            initMap.put("partnerAllList", partnerRepository.findCodeAll());
            return Header.OK(itemApiResponseList, initMap, pagination);
        }else{
            return Header.OK(itemApiResponseList,pagination);
        }
    }
}
