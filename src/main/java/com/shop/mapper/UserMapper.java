package com.shop.mapper;

import com.shop.dto.user.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    // 유저 찾기 + SNS (소셜) 유저 찾기
    UserDTO find_user(
            @Param("userDTO") UserDTO userDTO,
            @Param("isSNS") boolean isSNS
    );

    @Select("SELECT id FROM `user` WHERE `tel` = #{phoneNumber}")
    String find_user_id(String phoneNumber);

    // 유저 회원가입 ( 모든 정보 (SNS정보 빼고) )
    void join_user(UserDTO userDTO);
    // 기존 회원가입 유저에 SNS 정보만 등록하기
    void insert_sns_info(UserDTO userDTO);

    @Select("SELECT * FROM `user` WHERE `id` = #{id} AND `email` = #{email}")
    UserDTO find_user_by_email(UserDTO userDTO);

    // 유저가 패스워드 재설정 이메일의 링크를 클릭했다면 해당 토큰 값을 가진 유저를 가져오기
    @Select("SELECT * FROM `user` WHERE `pw_re_token` = #{token}")
    UserDTO find_user_by_token(String token);

    @Update("UPDATE `user` SET `password` = #{password} WHERE `pw_re_token` = #{pwReToken}")
    void update_user_password(UserDTO userDTO);

    @Update("UPDATE `user` SET `pw_re_token` = #{pwReToken}, `pw_re_token_expire` = #{pwReTokenExpire} WHERE `id` = #{id}")
    void update_user_repw_token(UserDTO userDTO);

    // 유저가 위시리스트(찜) 하기
    void insert_wishlist(
            @Param("userId") String userId,
            @Param("productNo") int productNo
    );
}
