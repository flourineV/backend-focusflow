package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.pomodoro.pomodoroDTO;
import com.example.focusflowbackend.dto.pomodorosession.pomodorosessionDTO;
import com.example.focusflowbackend.models.*;
import com.example.focusflowbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PomodoroService {

    private final PomodoroRepo pomodoroRepo;
    private final TaskRepo taskRepo;
    private final UserAccountRepo userRepo;
    private final PomodoroSessionRepo pomodoroSessionRepo;

    // ✅ Tạo mới Pomodoro
    public pomodoroDTO.Response createPomodoro(Long userId, pomodoroDTO.CreateRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Validate task ownership if taskId is provided
        Task task = validateTaskOwnership(userId, request.getTaskId());

        Pomodoro pomodoro = Pomodoro.builder()
                .user(user)
                .task(task)
                .sessionDate(request.getSessionDate() != null ? request.getSessionDate() : LocalDate.now())
                .focusTime(request.getFocusTime())
                .breakTime(request.getBreakTime())
                .totalTime(calculateTotalTime(request.getFocusTime(), request.getBreakTime()))
                .note(request.getNote())
                .isDeleted(false)
                .build();

        pomodoro = pomodoroRepo.save(pomodoro);
        return mapToResponse(pomodoro);
    }

    // ✅ Lấy danh sách Pomodoro của user
    public List<pomodoroDTO.Response> getPomodorosByUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return pomodoroRepo.findByUserId(userId)
                .stream()
                .filter(p -> !p.isDeleted())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Lấy Pomodoro theo ID
    public pomodoroDTO.Response getPomodoroById(Long userId, Long pomodoroId) {
        Pomodoro pomodoro = pomodoroRepo.findById(pomodoroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pomodoro not found"));

        if (!pomodoro.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return mapToResponse(pomodoro);
    }

    // ✅ Xóa mềm Pomodoro
    public void softDeletePomodoro(Long userId, Long pomodoroId) {
        Pomodoro pomodoro = pomodoroRepo.findById(pomodoroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pomodoro not found"));

        if (!pomodoro.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        pomodoro.setDeleted(true);
        pomodoroRepo.save(pomodoro);
    }

    // ✅ Lấy Pomodoro theo ngày
    public List<pomodoroDTO.Response> getPomodorosByDate(Long userId, LocalDate date) {
        return pomodoroRepo.findByUserIdAndSessionDate(userId, date)
                .stream()
                .filter(p -> !p.isDeleted())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Thêm session vào Pomodoro
    public pomodoroDTO.Response addSessionToPomodoro(Long userId, Long pomodoroId,
            pomodorosessionDTO.CreateRequest request) {
        Pomodoro pomodoro = pomodoroRepo.findById(pomodoroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pomodoro not found"));

        if (!pomodoro.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        PomodoroSession session = PomodoroSession.builder()
                .pomodoro(pomodoro)
                .duration(request.getDuration())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        pomodoroSessionRepo.save(session);
        return mapToResponse(pomodoro);
    }

    // ============ PRIVATE METHODS ============
    private Task validateTaskOwnership(Long userId, Long taskId) {
        if (taskId == null) {
            return null;
        }

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Task doesn't belong to user");
        }

        return task;
    }

    private Integer calculateTotalTime(Integer focusTime, Integer breakTime) {
        if (focusTime == null) {
            focusTime = 0;
        }
        if (breakTime == null) {
            breakTime = 0;
        }
        return focusTime + breakTime;
    }

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
                                .map(session -> pomodorosessionDTO.Response.builder()
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
