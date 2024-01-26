package com.shop.service;

import com.shop.dto.user.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.error("CustomOAuth2SuccessHandler");
        // 로그인 된 유저를 가져온다 ( 소셜 로그인 정보를 모두 가지고 있는 유저 객체 정보 )
        // 여기서 로그인 된 유저는 철저히 SNS유저 상태의 정보임.
        UserDTO loginedUser = (UserDTO) authentication.getPrincipal();

        // 현재 로그인 된 객체가 빈 껍데기 유저이다.
        if (loginedUser.getId().equals("temporary-custom-sns-user")) {
            // 로그인을 해제하고 회원가입을 처음부터 끝까지 진행시킨다 ...
            request.getSession().invalidate();
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("snsUser", loginedUser.getSnsInfo());
            response.sendRedirect("/user/join");
            return;
        }

        response.sendRedirect("/");
    }
}
