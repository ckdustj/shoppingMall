package com.shop.dto.product;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDTO {
    private int no;
    private int productNo;
    private String name;
    private int additionalPrice;
    private int amount;
}
