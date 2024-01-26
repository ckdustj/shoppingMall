package com.shop.dto.shopping;

import com.shop.dto.user.UserDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String merchantUid; // 결제건의 가맹점 주문번호
    private UserDTO user; // 결제건 주문자 정보 (구매자명, 구매자 전화번호, 이메일)
    private String addr; // 주문자 주소
    private String postCode; //주문자 우편번호
    private List<ShoppingCartDTO> shoppingCarts;
    private LocalDateTime createdDate;

    private int amount; // 결제건의 결제금액
    private int currency; //결제통화 구분코드 (KRW, USD, VND, ... Default: KRW)
    private int started_at; //결제 요청 시각
    private int cardQuota; //결제 할부개월 수 (일시불은 0) - 신용카드인경우
    private String payMethod; //결제건의 결제수단을 구분하는 코드
    private String pgProvider; //kakaopay 와 같은 결제 수단

    private int cardType; // 카드 구분코드 (0: 신용카드, 1: 체크카드, null: 카드정보제공하지않음)
    private String cardName; //결제건의 카드사명
    private String cardNumber; //카드번호

    private String impUid; //포트원 거래고유번호
    private String pgId;  // TC0ONETIME와 같은 결제건의 PG사 상점아이디
    private String pgTid; //결제건의 PG사 거래번호
}
