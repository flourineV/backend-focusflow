package com.example.focusflowbackend.dto.pomodoro;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.example.focusflowbackend.dto.pomodorosession.*;

public class pomodoroDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        private Long taskId;         // optional
        private LocalDate sessionDate;
        private Integer focusTime;
        private Integer breakTime;
        private Integer totalTime;
        private String note;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private Long taskId;
        private LocalDate sessionDate;
        private Integer focusTime;
        private Integer breakTime;
        private Integer totalTime;
        private String note;
        private LocalDateTime createdAt;
        private List<pomodorosessionDTO.Response> sessions;
    }
}
