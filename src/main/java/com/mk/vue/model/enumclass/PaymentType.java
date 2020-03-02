package com.mk.vue.model.enumclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {

    CASH(0, "현금", "현금")
    ,CARD(1, "카드", "카드")
    ,ETC(3, "기타", "기타")
    ;

    private Integer id;
    private String title;
    private String description;
}
