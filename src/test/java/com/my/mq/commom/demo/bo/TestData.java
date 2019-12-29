package com.my.mq.commom.demo.bo;


import com.my.mq.commom.demo.dto.JsonBase;
import java.util.Date;
import lombok.Data;

@Data
public class TestData extends JsonBase {
    private int age;
    private String name;
    private long createTime;
    private Date date;
    private Double price;
}
