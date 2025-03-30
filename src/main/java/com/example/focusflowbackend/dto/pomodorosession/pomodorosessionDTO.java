package com.example.focusflowbackend.dto.pomodorosession;

import lombok.*;

import java.time.LocalDateTime;

public class pomodorosessionDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        private Integer duration;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private Integer duration;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
}
