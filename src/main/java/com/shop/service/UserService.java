package com.shop.service;

import com.shop.dto.shopping.ShoppingCartDTO;
import com.shop.dto.user.UserDTO;
import com.shop.mapper.ShoppingCartMapper;
import com.shop.mapper.UserMapper;
import jakarta.mail.internet.MimeMessage;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {
    private final String NAVER_SMS_URL = "https://sens.apigw.ntruss.com/sms/v2/services/{serviceId}/messages";
    private final String NAVER_SMS_SERVICE_KEY = "";
    private final String NAVER_SMS_ACCESS_KEY = "";
    private final String NAVER_SMS_SECRET_KEY = "";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RestOperations restOperations;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    private final String SEND_EMAIL_FROM = "dustj3419@naver.com";
    private final String RESET_PASSWORD_URL = "http://localhost:8080/repw?token=";

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    // 회원가입
    public void join_user(UserDTO userDTO) {
        // 패스워드 인코딩 작업
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        // DB 삽입
        userMapper.join_user(userDTO);
    }

    // 유저 아이디 찾기
    public String find_user_id(String phoneNumber) {
        String userID = userMapper.find_user_id(phoneNumber);
        if (Objects.isNull(userID)) {
            return "유저 존재 X";
        }
        try {
            return "유저 ID :" + userID;
        } catch (Exception e) {
            return "일시적 문제...";
        }
    }

    private void send_sms_messsage(String phoneNumberTo) throws Exception {
        String timestamp = String.valueOf(Timestamp.valueOf(LocalDateTime.now()).getTime());            // current timestamp (epoch)
        String signature = makeSignature("POST", "/sms/v2/services/" + NAVER_SMS_SERVICE_KEY + "/messages", timestamp);
        // 받을 사람 등록
        JSONObject sendMessageTemplate = new JSONObject();
        sendMessageTemplate.put("to", phoneNumberTo);
        // 메세지 정보
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(sendMessageTemplate);
        // 보내는 메세지
        JSONObject messageObject = new JSONObject();
        messageObject.put("type", "SMS"); //필수
        messageObject.put("contentType", "COMM"); //필수 아님
        messageObject.put("from", "자기 번호");
        messageObject.put("content", "[KOREAIT]\n 조회할 아이디");
        messageObject.put("messages", Arrays.asList(sendMessageTemplate));
        // 메세지 전송 요청
        RequestEntity<String> requestEntity = RequestEntity
                .post(NAVER_SMS_URL, NAVER_SMS_SERVICE_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-ncp-apigw-timestamp", timestamp)
                .header("x-ncp-iam-access-key", NAVER_SMS_ACCESS_KEY)
                .header("x-ncp-apigw-signature-v2", signature)
                .body(messageObject.toString());

        ResponseEntity<String> response = restOperations.exchange(requestEntity, String.class);
    }

    public String makeSignature(String method, String url, String timestamp) throws Exception {
        String space = " ";                    // one space
        String newLine = "\n";                    // new line

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(NAVER_SMS_ACCESS_KEY)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(NAVER_SMS_SECRET_KEY.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    // 비밀번호를 재설정하는 페이지에서 유효한 유저의 검사 (+후 메일 전송)
    public String find_user_pw(UserDTO userDTO) {
        UserDTO findedUser = userMapper.find_user_by_email(userDTO);
        if (Objects.isNull(findedUser)) {
            return "유저 업써";
        }
        try {
            System.out.println("유저 찾음:" + userDTO);
            String token = UUID.randomUUID().toString();
            LocalDateTime tokenExpireDate = LocalDateTime.now().plusMinutes(2); // 토큰 만료 2분
            // 만들어진 토큰과 만료 날짜를 유저에게 설정한다
            findedUser.setPwReToken(token);
            findedUser.setPwReTokenExpire(tokenExpireDate);
            // DB에 업데이트 시킨다
            userMapper.update_user_repw_token(findedUser);
            // 메일 전송함
            send_mail_of_user_password(findedUser.getEmail(), token);
        } catch (Exception e) {
            return "관리자에게 연락하세요";
        }
        return "발송함 !";
    }

    // 비밀번호를 재설정하는 페이지의 유효한 token 검사
    public boolean find_user_by_token(String token) {
        // 타고 온 링크의 token 값으로 해당 유저가 존재하는지 검사
        UserDTO userDTO = userMapper.find_user_by_token(token);
        // 해당 유저가 존재하지 않거나, 토큰이 만료되었으면 실패!
        if (Objects.isNull(userDTO) || userDTO.getPwReTokenExpire().isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    // 유저 패스워드 변경하기
    public void update_user_pw(UserDTO userDTO) {
        userMapper.update_user_password(userDTO);
    }

    // 유저에게 비밀번호 변경 메일 (URL이 첨부되어있는) 전송하기
    public void send_mail_of_user_password(String to, String token) throws Exception {
        System.out.println("메일 보내기 시도");

        Context ctx = new Context();
        ctx.setVariable("link", RESET_PASSWORD_URL + token);

        String htmlContent = templateEngine.process("/mail/re-password.html", ctx);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(SEND_EMAIL_FROM); // 누가 보내나
        mimeMessageHelper.setTo(to); // 누구한테 보내나
        mimeMessageHelper.setSubject("hi"); // 제목은 뭔가
        mimeMessageHelper.setText(htmlContent, true); // 내용은 뭔가
        javaMailSender.send(mimeMessage);
    }

    // 유저의 장바구니 정보 가져오기
    public List<ShoppingCartDTO> get_shopping_cart_of_user(UserDTO userDTO) {
        return shoppingCartMapper.get_shopping_cart(userDTO, null);
    }

    // 장바구니에 상품 담기
    @Transactional
    public void add_product_in_shopping_cart(ShoppingCartDTO shoppingCartDTO) {
        shoppingCartMapper.insert_shopping_cart(shoppingCartDTO);
        if (Objects.nonNull(shoppingCartDTO.getProduct().getProductOptions())) {
            shoppingCartMapper.insert_shopping_cart_option(shoppingCartDTO);
        }
    }

    // 장바구니에 담긴 상품 하나의 수량 변경하기
    public void change_product_amount_of_shopping_cart(ShoppingCartDTO shoppingCartDTO){
        shoppingCartMapper.change_product_amount(shoppingCartDTO);
    }

    // 장바구니에 담긴 여러 상품 한번에 제거하기
    public void delete_product_in_shopping_cart(UserDTO userDTO, List<ShoppingCartDTO> shoppingCartDTOS){
        shoppingCartDTOS.forEach(shoppingCartDTO -> {
            shoppingCartDTO.setUser(userDTO);
        });
        shoppingCartMapper.delete_product(shoppingCartDTOS);
    }

}

