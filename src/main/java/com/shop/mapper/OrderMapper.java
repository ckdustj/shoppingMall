package com.shop.mapper;

import com.shop.dto.shopping.OrderDTO;
import com.shop.dto.user.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Mapper
public interface OrderMapper {
    OrderDTO get_order(OrderDTO orderDTO);
    void create_order(UserDTO userDTO);
    void create_order_cart(OrderDTO orderDTO);
}
