package com.mk.vue.controller.api;

import com.mk.vue.ifs.CrudInterface;
import com.mk.vue.model.network.Header;
import com.mk.vue.model.network.request.AdminUserApiRequest;
import com.mk.vue.model.network.response.AdminUserApiResponse;
import com.mk.vue.service.AdminUserApiLogicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/adminUser")
public class AdminUserApiController implements CrudInterface<AdminUserApiRequest, AdminUserApiResponse> {

    @Autowired
    private AdminUserApiLogicService adminUserApiLogicService;


    @Override
    @PostMapping("")
    public Header<AdminUserApiResponse> create(@RequestBody Header<AdminUserApiRequest> request) {
        log.info("{}",request);
        return adminUserApiLogicService.create(request);
    }

    @Override
    @GetMapping("/{id}")
    public Header<AdminUserApiResponse> read(@PathVariable(name = "id") Long id) {
        log.info("read id : {}",id);
        return adminUserApiLogicService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<AdminUserApiResponse> update(@RequestBody Header<AdminUserApiRequest> request) {
        return adminUserApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("/{id}")
    public Header delete(@PathVariable(name = "id") Long id) {
        log.info("delete : {}",id);
        return adminUserApiLogicService.delete(id);
    }

    @GetMapping("")
    public Header<List<AdminUserApiResponse>> findAll(@SortDefault.SortDefaults({
                                                                                 @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                 , @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                                                                }) Pageable pageable
                                                        ,@ModelAttribute AdminUserApiRequest adminUserApiRequest){
        log.info("{}",pageable);
        return adminUserApiLogicService.search(pageable, adminUserApiRequest);
    }
}
