package com.shop.controller;

import com.shop.dto.user.UserDTO;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {
    /* 메인 화면 이동 */
    @GetMapping("/")
    public String get_main(){
        return "main/main";
    }

    @Autowired
    private UserService userService;

    // 사용자가 메일 발송 버튼 눌렀을 때
    @ResponseBody
    @PostMapping("/find")
    public String find_user_pw(@RequestBody UserDTO userDTO){
        return userService.find_user_pw(userDTO);
    }

    // 패스워드 재설정 페이지로 이동
    @GetMapping("/repw")
    public String re_password_page(@RequestParam("token") String token, Model model){
        if (userService.find_user_by_token(token)){
            model.addAttribute("token", token);
            return "repw";
        }
        return "repw-expire";
    }

    // 유저 패스워드 변경하기
    @PostMapping("/repw")
    public String re_password(UserDTO userDTO){
        userService.update_user_pw(userDTO);
        // 맘대룽
        return "redirect:/";
    }
}
