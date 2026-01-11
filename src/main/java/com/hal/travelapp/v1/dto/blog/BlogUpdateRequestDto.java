package com.hal.travelapp.v1.dto.blog;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
@Setter
public class BlogUpdateRequestDto {
    
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    private MultipartFile mainPhoto;

    @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
    private String paragraph1;

    @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
    private String paragraph2;

    @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
    private String paragraph3;

    private MultipartFile midPhoto1;

    private MultipartFile midPhoto2;

    private MultipartFile midPhoto3;

    private MultipartFile sidePhoto;

    private Long cityId;

    private Long bestTimeStartMonth;

    private Long bestTimeEndMonth;

    private Set<Long> categoryIds;
}
