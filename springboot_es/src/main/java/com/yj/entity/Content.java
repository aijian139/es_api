package com.yj.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Content {

    private String img;

    private String price;

    private String name;
}
