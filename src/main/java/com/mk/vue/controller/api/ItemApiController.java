package com.mk.vue.controller.api;

import com.mk.vue.controller.CrudController;
import com.mk.vue.model.entity.Item;
import com.mk.vue.model.network.Header;
import com.mk.vue.model.network.request.ItemApiRequest;
import com.mk.vue.model.network.response.ItemApiResponse;
import com.mk.vue.service.ItemApiLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/item")
public class ItemApiController extends CrudController<ItemApiRequest, ItemApiResponse, Item> {

    @Autowired
    private ItemApiLogicService itemApiLogicService;


    @Override
    @PostMapping("")
    public Header<ItemApiResponse> create(@RequestBody Header<ItemApiRequest> request) {
        log.info("{}",request);
        return itemApiLogicService.create(request);
    }

    @Override
    @GetMapping("/{id}")
    public Header<ItemApiResponse> read(@PathVariable(name = "id") Long id) {
        log.info("read id : {}",id);
        return itemApiLogicService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<ItemApiResponse> update(@RequestBody Header<ItemApiRequest> request) {
        return itemApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("/{id}")
    public Header delete(@PathVariable(name = "id") Long id) {
        log.info("delete : {}",id);
        return itemApiLogicService.delete(id);
    }

    @GetMapping("")
    public Header<List<ItemApiResponse>> findAll(@SortDefault.SortDefaults({
                                                                            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                            , @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                                                        }) Pageable pageable
                                            , @ModelAttribute ItemApiRequest itemApiRequest
                                            , @RequestParam("initialYn") String initialYn){
        log.info("{}",pageable);
        return itemApiLogicService.search(pageable, itemApiRequest, initialYn);
    }
}
