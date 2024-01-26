package com.shop.dto.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Category {
    private int no;
    private String name;
    private int parent;
    private int level;
}
