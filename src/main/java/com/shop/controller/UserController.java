package com.shop.controller;

import com.shop.dto.shopping.ShoppingCartDTO;
import com.shop.dto.user.UserDTO;
import com.shop.service.PortOneService;
import com.shop.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private PortOneService userCertificationService;

    @GetMapping("/login")
    public void user_get_login(){    }

    @GetMapping("/join")
    public void user_get_join(){    }

    @PostMapping("/join")
    public String user_post_join(
            @Validated UserDTO userDTO,
            BindingResult result
    ){
        if(result.hasErrors()){
            log.error("userDTO 객체에 에러가 발생함!");
            return "redirect:/user/join";
        }
        userService.join_user(userDTO);
        log.info("User INSERTED");
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/cert/token")
    public String get_user_certification_token(){
        try{
            // 성공시에는 Token값 반환
            return userCertificationService.get_access_token();
        }catch (Exception e){
            // 실패시에는 빈문자열 반환
            log.error(e.getMessage());
            return "";
        }
    }

    @ResponseBody
    @PostMapping("/cert/{impUID}")
    public String get_user_certification_unique_key(
            @PathVariable String impUID,
            @RequestBody String token
    ){
        try{
            // 성공시에는 Token값 반환
            return userCertificationService.get_user_certification(impUID, token);
        }catch (Exception e){
            // 실패시에는 빈문자열 반환
            log.error(e.getMessage());
            return "";
        }
    }

    //***********************************************************
    // ID, PW 찾기 페이지 이동
    @GetMapping("/find")
    public String get_find_page(){
        return "user/findId";
    }


    @ResponseBody
    @GetMapping("/find/id/{phoneNumber}")
    public String find_user_id(@PathVariable String phoneNumber){
       return userService.find_user_id(phoneNumber);
    }

    ///***********************************************
    // 유저의 장바구니 화면으로 이동
    @GetMapping("/cart")
    public String get_shopping_cart_page(
            @AuthenticationPrincipal UserDTO userDTO,
            Model model
    ){
        List<ShoppingCartDTO> shoppingCartDTOS = userService.get_shopping_cart_of_user(userDTO);
        model.addAttribute("shoppingCarts", shoppingCartDTOS);
        System.out.println(shoppingCartDTOS);
        return "main/cart";
    }

    // 유저가 장바구니에 상품을 넣음
    @PostMapping("/cart")
    public String add_shopping_cart(
            @AuthenticationPrincipal UserDTO userDTO,
            ShoppingCartDTO shoppingCartDTO
    ){
        log.info("userDTO: " + userDTO);
        log.info("shoppingCartDTO: " + shoppingCartDTO);

        shoppingCartDTO.setUser(userDTO);
        userService.add_product_in_shopping_cart(shoppingCartDTO);
        // 유저 장바구니 창으로 이동시킨다
        return "redirect:/user/cart";
    }

    // 유저의 장바구니에 존재하는 상품의 수량 변경
    @ResponseBody
    @PatchMapping("/cart")
    public void change_product_amount_of_shopping_cart(
            @AuthenticationPrincipal UserDTO userDTO,
            ShoppingCartDTO shoppingCartDTO
    ){
        log.info(shoppingCartDTO);
        shoppingCartDTO.setUser(userDTO);
        userService.change_product_amount_of_shopping_cart(shoppingCartDTO);
//        return "redirect:/user/cart";
    }

    // 카트에 있는 상품 장바구니에서 제거하기
    @ResponseBody
    @DeleteMapping("/cart")
    public void delete_product_of_shopping_cart(
            @AuthenticationPrincipal UserDTO userDTO,
            List<ShoppingCartDTO> shoppingCartDTOS
    ){
        userService.delete_product_in_shopping_cart(userDTO, shoppingCartDTOS);
    }


}