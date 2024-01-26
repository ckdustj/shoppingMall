package com.shop.mapper;

import com.shop.dto.shopping.ShoppingCartDTO;
import com.shop.dto.user.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCartDTO> get_shopping_cart(
            @Param("user") UserDTO userDTO,
            @Param("cartNumbers") List<Integer> cartNumbers);
    void insert_shopping_cart(ShoppingCartDTO shoppingCartDTO);
    void insert_shopping_cart_option(ShoppingCartDTO shoppingCartDTO);
    void change_product_amount(ShoppingCartDTO shoppingCartDTO);
    void delete_product(List<ShoppingCartDTO> shoppingCartDTOS);
}
