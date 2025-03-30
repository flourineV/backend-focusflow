package com.example.focusflowbackend.dto.subtask;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class subtaskDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "Tiêu đề không được để trống")
        private String title;

        @NotNull(message = "Task ID không được để trống")
        private Long taskId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTitle {

        @NotBlank(message = "Tiêu đề không được để trống")
        private String title;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCompletion {

        private boolean isCompleted;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private String title;
        private boolean isCompleted;
        private Long taskId; // Để biết subtask thuộc về task nào
    }
}
