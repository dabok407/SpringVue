package com.mk.vue.controller.api;

import com.mk.vue.ifs.CrudInterface;
import com.mk.vue.model.network.Header;
import com.mk.vue.model.network.request.PartnerApiRequest;
import com.mk.vue.model.network.response.PartnerApiResponse;
import com.mk.vue.service.PartnerApiLogicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/partner")
public class PartnerApiController implements CrudInterface<PartnerApiRequest, PartnerApiResponse> {

    /*extends CrudController<PartnerApiRequest, PartnerApiResponse, Partner>*/

    @Autowired
    private PartnerApiLogicService partnerApiLogicService;

    @Override
    @PostMapping("")
    public Header<PartnerApiResponse> create(@RequestBody Header<PartnerApiRequest> request) {
        log.info("{}", request);
        return partnerApiLogicService.create(request);
    }

    @Override
    @GetMapping("{id}")
    public Header<PartnerApiResponse> read(@PathVariable(name = "id") Long id) {
        log.info("read id : {}", id);
        return partnerApiLogicService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<PartnerApiResponse> update(@RequestBody Header<PartnerApiRequest> request) {
        return partnerApiLogicService.update(request);
    }

    @Override
    @DeleteMapping("{id}")
    public Header delete(@PathVariable Long id) {
        log.info("delete : {}",id);
        return partnerApiLogicService.delete(id);
    }

    @GetMapping("")
    public Header<List<PartnerApiResponse>> findAll(@SortDefault.SortDefaults({
                                                                                @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                , @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                                                                }) Pageable pageable
                                                    ,@ModelAttribute PartnerApiRequest partnerApiRequest){
        log.info("{}",pageable);
        return partnerApiLogicService.search(pageable, partnerApiRequest);
    }
}


