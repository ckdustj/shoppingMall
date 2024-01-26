package com.shop.service;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import java.util.Map;

@Service
public class PortOneService {
    // ************************** PAYMENT 인증
    @Autowired
    private RestOperations restOperations;

    ////////////////////// PORTONE 서비스 관련 KEY /////////////////////////////////////
    private final String PORTONE_API_KEY = "8126181711377774";
    private final String PORTONE_API_SECRET = "9S3JPX3pxQhqlj7acuNTppgsv5K8gDfpiGozFMu5sTcyDjWI3UEUV0iq1HTaekVh1VnNqwISQgsjkO97";
    // ACCESS_TOKEN 발급 URL
    String PORTONE_ACCESS_TOKEN_URL = "https://api.iamport.kr/users/getToken";
    /////////////////////// 인증 관련 API
    private final String PORTONE_CERTIFICATION_CI_URI = "https://api.iamport.kr/certifications/{impUID}";
    /////////////////////// 결제 관련 API
    // 결제 금액 사전등록 API URL
    String PORTONE_PAYMENTS_PREPARE_URL = "https://api.iamport.kr/payments/prepare";
    // 결제내역 단건조회 API URL - 포트원 거래고유번호로 결제내역을 확인합니다
    String PORTONE_PAYMENTS_INQUERY_URL = "https://api.iamport.kr//payments/{imp_uid}";

    // PORT ONE 기능을 사용하기 위ㅏ해 ACCESS_TOKEN 발급받기
    public String get_access_token(){
        Map<String, String> bodyData = Map.of(
                "imp_key", PORTONE_API_KEY,
                "imp_secret", PORTONE_API_SECRET
        );
        RequestEntity<String> requestEntity = RequestEntity
                .post(PORTONE_ACCESS_TOKEN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JSONObject.toJSONString(bodyData));
        ResponseEntity<JSONObject> responseEntity = restOperations.exchange(requestEntity, JSONObject.class);
        JSONObject responseBody = responseEntity.getBody();
        Map data = ((Map)responseBody.get("response"));
        return (String)data.get("access_token");
    }

    //////////////////////////////////////////////////////////////////////////
    // 유저 인증 후, 유저의 CI 값 받기
    public String get_user_certification(String impUID, String token) throws Exception{
        RequestEntity requestEntity = RequestEntity
                .get(PORTONE_CERTIFICATION_CI_URI, impUID)
                .header("Authorization", token)
                .build();

        ResponseEntity<JSONObject> responseEntity = restOperations.exchange(requestEntity, JSONObject.class);
        System.out.println(responseEntity.getBody());
        return ((Map) responseEntity.getBody().get("response")).get("unique_key").toString();
    }

    // 결제 요청 전, 사전 검증 요청하기 (내가 결제할 상품의 정보를 PORT_ONE에 전달해놓기)
    // merchant_uid => 한 주문의 고유 주문번호 / amount => 총 결제금액
    public String pre_verification_order(String merchant_uid, Integer amount) {
        String accessToken = get_access_token();

        Map<String, Object> bodyData = Map.of(
                "merchant_uid", merchant_uid,
                "amount", amount
        );

        RequestEntity<String> requestEntity = RequestEntity
                .post(PORTONE_ACCESS_TOKEN_URL)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JSONObject.toJSONString(bodyData));
        ResponseEntity<JSONObject> responseEntity = restOperations.exchange(requestEntity, JSONObject.class);
        JSONObject responseBody = responseEntity.getBody();
        // 0이면 정상적인 조회, 0아닌 값이면 message를 확인해봐야 합니다
        int code = (Integer) responseBody.get("code");
        // code값이 0이 아닐 때, '존재하지 않는 결제정보입니다'와 같은 오류 메세지를 포함합니다
        return code == 0 ? null : (String) responseBody.get("message");
//        System.out.println(((Map)responseBody.get("response")).get("merchant_uid"));
//        System.out.println(((Map)responseBody.get("response")).get("amount"));
    }

    // 결제 내역 단건 조회하기 (+결제 요청 후, 사후 검증 요청하기)
    // imp_uid => PORT_ONE에 등록된 고유한 결제 번호
    public Map<Boolean, ? extends Object> get_inquery_order(String imp_uid) {
        String accessToken = get_access_token();

        Map<String, Object> bodyData = Map.of(
                "imp_uid", imp_uid
        );
        RequestEntity<String> requestEntity = RequestEntity
                .post(PORTONE_PAYMENTS_INQUERY_URL, imp_uid)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JSONObject.toJSONString(bodyData));
        ResponseEntity<JSONObject> responseEntity = restOperations.exchange(requestEntity, JSONObject.class);
        JSONObject responseBody = responseEntity.getBody();
        // 0이면 정상적인 조회, 0아닌 값이면 message를 확인해봐야 합니다
        // code값이 0이 아닐 때, '존재하지 않는 결제정보입니다'와 같은 오류 메세지를 포함합니다
        int code = (Integer) responseBody.get("code");
        if(code == 0){
            Map responseData = (Map) responseBody.get("response");
            return Map.of(true, responseData);
        }else{
            String message = (String) responseBody.get("message");
            return Map.of(false, message);
        }
    }

}
