package com.demo.order;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderCodeGenerator {

    private int i = 0;

    public String getOrderCode() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss-");
        return sdf.format(now) + ++i;
    }

}
