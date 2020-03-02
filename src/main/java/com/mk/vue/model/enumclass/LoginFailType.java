package com.mk.vue.model.enumclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginFailType {

    AUTH_FAIL("01","인증 거부","계정 인증 거부"),
    BAD_CREDENTIALS("02","비밀번호 불일치","비밀번호 불일치"),
    LOCKED_ACCOUNT("03","잠긴 계정","잠긴계정"),
    DISABLED_ACCOUNT("04","비활성화 계정","비활성화 계정"),
    EXPIRED_ACCOUNT("05","계정 유효기간 만료","계정 유효기간 만료"),
    EXPIRED_PASSWORD("06","비밀번호 유효기간 만료","비밀번호 유효기간 만료"),
    NONE_ACCOUNT("07","계정정보 없음","계정정보 없음")
    ;

    private String id;
    private String title;
    private String msg;
}
