package com.shop.service;

import com.shop.dto.user.SnsInfoDTO;
import com.shop.dto.user.UserDTO;
import com.shop.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Log4j2
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.warn(username);
        SnsInfoDTO snsInfoDTO = SnsInfoDTO.builder().build();
        UserDTO tempUserDTO = UserDTO.builder().id(username).snsInfo(snsInfoDTO).build();
        // 여기는 일반 로그인이기 떄문에 isSNS가 false로 들어가야 함 (user의 id로 조회한다)
        UserDTO userDTO = userMapper.find_user(tempUserDTO, false);
        if(Objects.isNull(userDTO)){
            throw new UsernameNotFoundException("해당 username이 존재하지 않음, [" + username + "]");
        }
        return userDTO;
    }
}
