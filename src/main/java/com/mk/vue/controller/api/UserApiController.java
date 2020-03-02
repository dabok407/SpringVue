package com.mk.vue.controller.api;

import com.mk.vue.ifs.CrudInterface;
import com.mk.vue.model.network.Header;
import com.mk.vue.model.network.request.UserApiRequest;
import com.mk.vue.model.network.response.UserApiResponse;
import com.mk.vue.model.network.response.UserOrderInfoApiResponse;
import com.mk.vue.service.UserApiLogicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserApiController implements CrudInterface<UserApiRequest, UserApiResponse> {

    @Autowired
    private UserApiLogicService userApiLogicService;

    @GetMapping("/{id}/orderInfo")
    public Header<UserOrderInfoApiResponse> orderInfo(@PathVariable Long id){
        return userApiLogicService.orderInfo(id);
    }

    @Override
    @PostMapping("")    // /api/user
    public Header<UserApiResponse> create(@RequestBody Header<UserApiRequest> request) {
        log.info("{}",request);
        return userApiLogicService.create(request);
    }

    @Override
    @GetMapping("{id}") // /api/user/{id}
    public Header<UserApiResponse> read(@PathVariable(name = "id") Long id) {
        log.info("read id : {}",id);
        return userApiLogicService.read(id);
    }

    @Override
    @PutMapping("") // /api/user
    public Header<UserApiResponse> update(@RequestBody Header<UserApiRequest> request) {
        return userApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("{id}")  // /api/user/{id}
    public Header delete(@PathVariable Long id) {
        log.info("delete : {}",id);
        return userApiLogicService.delete(id);
    }

    @GetMapping("")
    public Header<List<UserApiResponse>> findAll(@SortDefault.SortDefaults({
                                                                            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                            , @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                                                            }) Pageable pageable
                                                ,@ModelAttribute UserApiRequest userApiRequest){
        log.info("{}",pageable);
        return userApiLogicService.search(pageable, userApiRequest);
    }

    @GetMapping("like/{account}")
    public Header<List<UserApiResponse>> findLikeAll(@PathVariable String account){
        return userApiLogicService.search(account);
    }
}
