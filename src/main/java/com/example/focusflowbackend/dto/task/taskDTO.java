package com.example.focusflowbackend.dto.task;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.List;
import java.time.LocalDateTime;

public class taskDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "Tiêu đề không được để trống")
        private String title;

        @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
        private String description;

        private LocalDateTime dueDate;

        @NotBlank(message = "Trạng thái không được để trống")
        private String status;

        @Min(value = 1, message = "Priority phải từ 1 trở lên")
        @Max(value = 5, message = "Priority tối đa là 5")
        private Integer priority;

        private String repeatStyle;

        @Min(value = 0, message = "Ngày nhắc nhở phải >= 0")
        private Integer reminderDaysBefore;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatus {

        @NotBlank(message = "Trạng thái không được để trống")
        private String status;
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
    public static class UpdatePriority {

        @Min(value = 1, message = "Priority phải từ 1 trở lên")
        @Max(value = 5, message = "Priority tối đa là 5")
        private Integer priority;
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
    public static class UpdateDescription {

        @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDueDate {

        private LocalDateTime dueDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRepeatStyle {

        private String repeatStyle;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReminder {

        @Min(value = 0, message = "Ngày nhắc nhở phải >= 0")
        private Integer reminderDaysBefore;
    }

    @Data
    @Builder
    public static class Response {

        private Long id;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private String status;
        private String priority;
        private String repeatStyle;
        private Integer reminderDaysBefore;
        private boolean isCompleted;
        private Long projectId;
        private List<Long> tagIds;
    }
}
