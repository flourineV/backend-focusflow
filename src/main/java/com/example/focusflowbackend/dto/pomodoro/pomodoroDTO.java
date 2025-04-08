package com.example.focusflowbackend.dto.pomodoro;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.example.focusflowbackend.dto.pomodorosession.*;

public class pomodoroDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        private Long taskId;    // optional
        private Integer focusTime;
        private Integer breakTime;
        private Integer totalTime;
        private String note;
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
        private Long taskId;
        private Integer focusTime;
        private Integer breakTime;
        private Integer totalTime;
        private String note;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private LocalDateTime createdAt;
        private List<pomodorosessionDTO.Response> sessions;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateNote {

        private String note;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateIsDeleted {

        private boolean isDeleted;
    }
}
