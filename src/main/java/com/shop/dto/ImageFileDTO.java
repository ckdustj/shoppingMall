package com.shop.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString(exclude = "data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageFileDTO {
    private int no;
    private byte[] data;
    private MultipartFile file;
    private String originalFileName;
    private String savedFileName;
}
