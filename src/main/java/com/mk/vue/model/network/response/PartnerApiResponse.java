package com.mk.vue.model.network.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerApiResponse {

    private Long id;

    private String name;

    private String status;

    private String address;

    private String callCenter;

    private String partnerNumber;

    private String businessNumber;

    private String ceoName;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd (HH:mm:ss)", timezone="Asia/Seoul")
    private LocalDateTime registeredAt;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd (HH:mm:ss)", timezone="Asia/Seoul")
    private LocalDateTime unregisteredAt;

    private Long categoryId;
}
