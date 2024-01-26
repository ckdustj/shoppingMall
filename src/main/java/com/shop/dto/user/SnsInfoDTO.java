package com.shop.dto.user;


import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SnsInfoDTO {
    private String id;
    private String userId;
    private String clientName;
    private Map<String, Object> attributes;
    private LocalDateTime connectDate;
}
