package com.yj.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Content {

    private String img;

    private String name;

    private String price;
}
