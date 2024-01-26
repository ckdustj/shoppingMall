package com.shop.service;

import com.shop.dto.ImageFileDTO;
import com.shop.dto.user.SnsInfoDTO;
import com.shop.dto.user.UserDTO;
import com.shop.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Log4j2
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    // --- 배포 단계에서는 제거해야 합니다.. ---
    private final String USER_CI = "lOVSSALEXg5cU3EG3Xjk9tdb/lW2nw94/4wDD6k61rgWd9od96tRih+tV7s3zyYoCphTJoMVWIi2R+9RgzoouA==";


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.warn(" =========== CustomOAuth2UserService ============ ");
        log.warn(userRequest);
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();
        log.warn("[" + clientName + "]" + "(으)로 로그인 중입니다...");
        // 정보를 담고 있는 유저 맵을 생성한다.
        Map<String, Object> userPropertiesMap = create_user_properties_map();

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();

        switch (clientName.toUpperCase()){
            case "KAKAO" -> get_kakao_properties(paramMap, userPropertiesMap);
            case "GOOGLE" -> get_google_properties(paramMap, userPropertiesMap);
            case "NAVER" -> get_naver_properties(paramMap, userPropertiesMap);
        }

        // 로그인 된 유저를 가져온다 ( 소셜 로그인 정보를 모두 가지고 있는 유저 객체 정보 )
        // 여기서 로그인 된 유저는 철저히 SNS유저 상태의 정보임.
        UserDTO loginedUser = create_userDTO(userPropertiesMap, userRequest.getAccessToken().getTokenValue());
        loginedUser.getSnsInfo().setClientName(clientName);

        // 로그인 된 유저의 정보로 DB에서 탐색한다 ( 해당 소셜의 정보만 )
        UserDTO findedUserDTO = userMapper.find_user(loginedUser, true);
        // 근데 유저 자체도 없네 ?? => 유저 회원가입(첫 등록)도 해야 한다.
        if(Objects.isNull(findedUserDTO)) {
            // 여기서 finedUserDTO는 기존 회원정보가 없는 빈 껍데기. 로그인 한 유저로 반환 (id=sns-user)
            return loginedUser;
        }

        // 그 소셜 회원가입(등록) 기록이 있는가? 기록이 없었다. => 소셜 회원가입(등록) 해야 한다.
        if(Objects.isNull(findedUserDTO.getSnsInfo())) {
            // 찾은 기본 유저 정보 + 현재 로그인한 SNS 정보 합체
            findedUserDTO.setSnsInfo(loginedUser.getSnsInfo());
            // 새로운 소셜 로그인만 등록하면 된다. 유저 정보에 새로운 소셜 정보를 추가한다...
            userMapper.insert_sns_info(findedUserDTO);
        }

        // 현재 로그인 한 유저를 DB 유저로 교체한다
        return findedUserDTO;
    }

    public Map<String, Object> create_user_properties_map(){
        Map<String, Object> userPropertiesMap = new HashMap<>();
        userPropertiesMap.put("id", null);
        userPropertiesMap.put("name", null);
        userPropertiesMap.put("email", null);
        userPropertiesMap.put("mobile", null);
        userPropertiesMap.put("profile_image", null);
        return userPropertiesMap;
    }

    public void get_kakao_properties(Map<String, Object> paramMap, Map<String, Object> userPropertiesMap){
        Map<String, String> propertyMap = (Map<String, String>) paramMap.get("properties");
        String id = (String) paramMap.get("id").toString();
        String name = propertyMap.get("nickname"); // 닉네임 (이름)
        String profileImage = propertyMap.get("profile_image"); // 프로필 사진

        userPropertiesMap.put("id", id);
        userPropertiesMap.put("name", name);
        userPropertiesMap.put("profileImage", profileImage);
    }

    public void get_google_properties(Map<String, Object> paramMap, Map<String, Object> userPropertiesMap){
        String id = (String) paramMap.get("sub"); // id
        String name = (String) paramMap.get("name"); // 이름
        String email = (String) paramMap.get("email"); // 이메일
        String profileImage = (String) paramMap.get("picture"); // 프로필 사진

        userPropertiesMap.put("id", id);
        userPropertiesMap.put("name", name);
        userPropertiesMap.put("email", email);
        userPropertiesMap.put("profileImage", profileImage);
    }

    public void get_naver_properties(Map<String, Object> paramMap, Map<String, Object> userPropertiesMap){
        Map<String, String> propertyMap = (Map<String, String>) paramMap.get("response");
        String id = propertyMap.get("id");
        String email = propertyMap.get("email");
        String mobile = propertyMap.get("mobile");
        String profileImage = propertyMap.get("profile_image");

        userPropertiesMap.put("id", id);
        userPropertiesMap.put("email", email);
        userPropertiesMap.put("mobile", mobile);
        userPropertiesMap.put("profileImage", profileImage);
    }

    public UserDTO create_userDTO(Map<String, Object> userPropertiesMap, String token){
        String snsInfoId = (String) userPropertiesMap.get("id");
        String snsInfoClientName = (String) userPropertiesMap.get("clientName");
        String snsInfoProfileImage= (String) userPropertiesMap.get("profile_image");
        String snsInfoMobile = (String) userPropertiesMap.get("mobile");
        String snsInfoEmail = (String) userPropertiesMap.get("email");

        SnsInfoDTO snsInfoDTO = SnsInfoDTO.builder()
                .id(snsInfoId)
                .clientName(snsInfoClientName)
                .connectDate(LocalDateTime.now())
                .attributes(userPropertiesMap)
                .build();

        ImageFileDTO imageFileDTO = ImageFileDTO.builder()
                .originalFileName(snsInfoProfileImage)
                .savedFileName(snsInfoProfileImage)
                .build();

        return UserDTO.builder()
                .id("temporary-custom-sns-user")
                .ci(USER_CI)
                .token(token)
                .tel(snsInfoMobile)
                .imageFile(imageFileDTO)
                .email(snsInfoEmail)
                .snsInfo(snsInfoDTO)
                .build();
    }
}

