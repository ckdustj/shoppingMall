package com.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/patment")
public class PaymentController {
    @GetMapping("")
    public void get_payment_view(
            @RequestParam List<Integer> cartNo,
            Model model
    ){

    }

    @PostMapping("")
    public void post_payment_process(){

    }

}
