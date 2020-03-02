package com.mk.vue.controller.api;

import com.mk.vue.ifs.CrudInterface;
import com.mk.vue.model.network.Header;
import com.mk.vue.model.network.request.OrderGroupApiRequest;
import com.mk.vue.model.network.response.OrderGroupApiResponse;
import com.mk.vue.service.OrderGroupApiLogicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orderGroup")
public class OrderGroupApiController implements CrudInterface<OrderGroupApiRequest, OrderGroupApiResponse> {
    /*extends CrudController<OrderGroupApiRequest, OrderGroupApiResponse, OrderGroup>*/

    @Autowired
    private OrderGroupApiLogicService orderGroupApiLogicService;

    @Override
    @PostMapping("")
    public Header<OrderGroupApiResponse> create(@RequestBody Header<OrderGroupApiRequest> request) {
        log.info("{}", request);
        return orderGroupApiLogicService.create(request);
    }

    @Override
    @GetMapping("{id}")
    public Header<OrderGroupApiResponse> read(@PathVariable Long id) {
        log.info("{read id}", id);
        return orderGroupApiLogicService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<OrderGroupApiResponse> update(@RequestBody Header<OrderGroupApiRequest> request) {
        return orderGroupApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("{id}")
    public Header delete(@PathVariable(name = "id") Long id) {
        log.info("delete : {}", id);
        return orderGroupApiLogicService.delete(id);
    }

    @GetMapping("")
    public Header<List<OrderGroupApiResponse>> findAll(@SortDefault.SortDefaults({
                                                                                @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                , @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                                                                }) Pageable pageable
                                                    , @ModelAttribute OrderGroupApiRequest orderGroupApiRequest
                                                    , @RequestParam("initialYn") String initialYn){
        log.info("{}", pageable);
        return orderGroupApiLogicService.search(pageable, orderGroupApiRequest, initialYn);
    }

}
