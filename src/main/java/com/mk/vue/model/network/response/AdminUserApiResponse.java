package com.mk.vue.model.network.response;

import com.mk.vue.model.enumclass.AdminUserStatus;
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
public class AdminUserApiResponse {

    private Long id;

    private String account;

    private String password;

    private AdminUserStatus status;

    private String role;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd (HH:mm:ss)", timezone="Asia/Seoul")
    private LocalDateTime lastLoginAt;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd (HH:mm:ss)", timezone="Asia/Seoul")
    private LocalDateTime passwordUpdatedAt;

    private int loginFailCount;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd (HH:mm:ss)", timezone="Asia/Seoul")
    private LocalDateTime registeredAt;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd (HH:mm:ss)", timezone="Asia/Seoul")
    private LocalDateTime unregisteredAt;

}
