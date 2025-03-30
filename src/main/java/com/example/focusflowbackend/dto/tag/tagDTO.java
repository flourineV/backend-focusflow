package com.example.focusflowbackend.dto.tag;

import lombok.*;
import jakarta.validation.constraints.*;

public class tagDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "Tên tag không được để trống")
        private String name;
    }

    @Data
    @Builder
    public static class Response {

        private Long id;
        private String name;
    }
}
