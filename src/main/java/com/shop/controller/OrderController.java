package com.shop.controller;

import com.shop.dto.shopping.ShoppingCartDTO;
import com.shop.dto.user.UserDTO;
import com.shop.service.OrderService;
import com.shop.service.PortOneService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired private OrderService orderService;
    @Autowired private PortOneService portOneService;

    // 주문/결제 정보 창 이동
    @PostMapping
    public String get_cart_items_of_cart(
            @AuthenticationPrincipal UserDTO userDTO,
            @RequestParam(value = "no") List<Integer> cartNumbers,
            Model model
    ){
        List<ShoppingCartDTO> shoppingCartDTOS = orderService.get_order_of_shopping_cart(cartNumbers, userDTO);
        model.addAttribute("shoppingCarts", shoppingCartDTOS);
        return "main/pay";
    }

//    // 장바구니에 있는 내용들을 주문/결제 목록(DB)에 넣고 주문/결제 창으로 이동
//    @PostMapping
//    @ResponseBody
//    public ResponseEntity<Boolean> post_order_of_cart(
//            @AuthenticationPrincipal UserDTO userDTO,
//            @RequestParam("no") List<Integer> cartNumbers
//    ){
//        if (orderService.create_order(cartNumbers, userDTO)){
//            return ResponseEntity.ok().body(false);
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).body(true);
//    }

    ///////////////결제 관련////////////////
    // 결제 요청 전 결제 정보 PORTONE에 등록해놓기
    @ResponseBody
    @PostMapping("/payment/pre_verification")
    public ResponseEntity<String> post_pre_verification(
            @RequestParam("merchant_uid") String merchant_uid,
            @RequestParam("amount") Integer amount
    ){
        String message = portOneService.pre_verification_order(merchant_uid, amount);
        return Objects.isNull(message) ?
                ResponseEntity.status(HttpStatus.OK).body(message) :
                ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @ResponseBody
    @GetMapping("/payment/{imp_uid}")
    public ResponseEntity<String> get_inquery_order(
            @AuthenticationPrincipal UserDTO userDTO,
            @PathVariable("imp_uid") String imp_uid,
            @RequestParam("no") List<Integer> cartNumbers
    ){
        Map<Boolean, ? extends Object> resultObject = portOneService.get_inquery_order(imp_uid);
        boolean result = resultObject.keySet().iterator().next();
        // 값 가져오기 성공!
        if(result){
            // DB에 삽입합니다.!
            Map response = (Map) resultObject.get(result);
            orderService.create_order(userDTO, response, cartNumbers);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        // 값 가져오기 실패!
        String message = (String) resultObject.get(result);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
    }






}
