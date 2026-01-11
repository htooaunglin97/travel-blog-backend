package com.hal.travelapp.v1.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
@Setter
public class BlogCreateRequestDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    private MultipartFile mainPhoto;

    @NotBlank(message = "First paragraph is required")
    @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
    private String paragraph1;

    @NotBlank(message = "Second paragraph is required")
    @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
    private String paragraph2;

    @NotBlank(message = "Third paragraph is required")
    @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
    private String paragraph3;

    private MultipartFile midPhoto1;

    private MultipartFile midPhoto2;

    private MultipartFile midPhoto3;

    private MultipartFile sidePhoto;

    @NotNull(message = "City ID is required")
    private Long cityId;

    private Long bestTimeStartMonth;

    private Long bestTimeEndMonth;

    private Set<Long> categoryIds;
}
