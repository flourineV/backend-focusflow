package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.pomodoro.pomodoroDTO;
import com.example.focusflowbackend.dto.pomodorosession.pomodorosessionDTO;
import com.example.focusflowbackend.models.Pomodoro;
import com.example.focusflowbackend.models.Task;
import com.example.focusflowbackend.models.User;
import com.example.focusflowbackend.repository.PomodoroRepo;
import com.example.focusflowbackend.repository.TaskRepo;
import com.example.focusflowbackend.repository.UserAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PomodoroService {

    private final PomodoroRepo pomodoroRepo;
    private final TaskRepo taskRepo;
    private final UserAccountRepo userRepo;

    // ✅ Tạo mới Pomodoro
    public pomodoroDTO.Response createPomodoro(Long userId, pomodoroDTO.CreateRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Task task = null;
        if (request.getTaskId() != null) {
            task = taskRepo.findById(request.getTaskId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        }

        Pomodoro pomodoro = Pomodoro.builder()
                .user(user)
                .task(task)
                .sessionDate(request.getSessionDate())
                .focusTime(request.getFocusTime())
                .breakTime(request.getBreakTime())
                .totalTime(request.getTotalTime())
                .note(request.getNote())
                .isDeleted(false)
                .build();

        pomodoro = pomodoroRepo.save(pomodoro);

        return mapToResponse(pomodoro);
    }

    // ✅ Lấy danh sách Pomodoro của user
    public List<pomodoroDTO.Response> getPomodorosByUser(Long userId) {
        return pomodoroRepo.findByUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Mapping
    private pomodoroDTO.Response mapToResponse(Pomodoro pomodoro) {
        return pomodoroDTO.Response.builder()
                .id(pomodoro.getId())
                .taskId(pomodoro.getTask() != null ? pomodoro.getTask().getId() : null)
                .sessionDate(pomodoro.getSessionDate())
                .focusTime(pomodoro.getFocusTime())
                .breakTime(pomodoro.getBreakTime())
                .totalTime(pomodoro.getTotalTime())
                .note(pomodoro.getNote())
                .createdAt(pomodoro.getCreatedAt())
                .sessions(pomodoro.getSessions() != null
                        ? pomodoro.getSessions().stream()
                                .map(session -> PomodoroSessionDTO.Response.builder()
                                .id(session.getId())
                                .duration(session.getDuration())
                                .startTime(session.getStartTime())
                                .endTime(session.getEndTime())
                                .build())
                                .collect(Collectors.toList())
                        : null)
                .build();
    }
}
