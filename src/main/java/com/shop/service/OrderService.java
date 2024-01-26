package com.shop.service;

import com.shop.dto.shopping.OrderDTO;
import com.shop.dto.shopping.ShoppingCartDTO;
import com.shop.dto.user.UserDTO;
import com.shop.mapper.OrderMapper;
import com.shop.mapper.ShoppingCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    // 장바구니 창에서 주문창으로 이동할 때 장바구니의 특정 상품만을 가져오는 서비스
    public List<ShoppingCartDTO> get_order_of_shopping_cart(List<Integer> orderNumbers, UserDTO userDTO){
        return shoppingCartMapper.get_shopping_cart(userDTO, orderNumbers);
    }

    @Transactional
    public boolean create_order(UserDTO userDTO, Map<String, Object> orderDataMap, List<Integer> cartNumbers){
        OrderDTO.builder()
                .user(userDTO)
                .shoppingCarts(cartNumbers.parallelStream()
                                .map(number -> ShoppingCartDTO.builder().no(number).build())
                                .toList()
                )
                .createdDate(LocalDateTime.now())
                .addr((String) orderDataMap.get("addr"))
                .impUid((String)orderDataMap.get("imp_uid"))
                .postCode((String)orderDataMap.get("post_code"))
                .amount((Integer) orderDataMap.get("amount"))
                .currency((Integer)orderDataMap.get("currency"))
                .started_at((Integer)orderDataMap.get("started_at"))
                .cardQuota((Integer)orderDataMap.get("card_quota"))
                .payMethod((String)orderDataMap.get("pay_method"))
                .pgProvider((String)orderDataMap.get("pg_provider"))
                .cardType((Integer)orderDataMap.get("card_type"))
                .cardName((String)orderDataMap.get("card_name"))
                .cardNumber((String)orderDataMap.get("card_number"))
                .pgId((String)orderDataMap.get("pg_id"))
                .build();
//        orderMapper.create_order(userDTO);
        return true;
    }
}
