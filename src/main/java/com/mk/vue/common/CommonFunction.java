package com.mk.vue.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CommonFunction {

    public static String convertCommaMoney(String money){
        DecimalFormat dc = new DecimalFormat("###,###,###,###.####");
        BigDecimal bigDecimal = new BigDecimal(money);
        return dc.format(bigDecimal);
    }
}
