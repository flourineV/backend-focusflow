package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.pomodorosession.pomodorosessionDTO;
import com.example.focusflowbackend.models.Pomodoro;
import com.example.focusflowbackend.models.PomodoroSession;
import com.example.focusflowbackend.repository.PomodoroRepo;
import com.example.focusflowbackend.repository.PomodoroSessionRepo;
import com.example.focusflowbackend.repository.TaskRepo;
import com.example.focusflowbackend.repository.UserAccountRepo;
import com.example.focusflowbackend.models.Task;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PomodoroSessionService {

    private final PomodoroSessionRepo pomodoroSessionRepo;
    private final PomodoroRepo pomodoroRepo;
    private final TaskRepo taskRepo;
    private final UserAccountRepo userRepo;

    // ✅ Tạo mới một session trong Pomodoro
    public pomodorosessionDTO.Response createSession(Long userId, Long pomodoroId, pomodorosessionDTO.CreateRequest request) {
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
        return mapToResponse(session);
    }

    // ✅ Lấy danh sách sessions theo Pomodoro ID
    public List<pomodorosessionDTO.Response> getSessionsByPomodoroId(Long userId, Long pomodoroId) {
        Pomodoro pomodoro = pomodoroRepo.findById(pomodoroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pomodoro not found"));

        if (!pomodoro.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return pomodoroSessionRepo.findByPomodoroId(pomodoroId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Xóa session theo ID
    public void deleteSession(Long userId, Long sessionId) {
        PomodoroSession session = pomodoroSessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        if (!session.getPomodoro().getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        pomodoroSessionRepo.delete(session);
    }

    // ============ PRIVATE METHODS ============
    private pomodorosessionDTO.Response mapToResponse(PomodoroSession session) {
        return pomodorosessionDTO.Response.builder()
                .id(session.getId())
                .duration(session.getDuration())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .build();
    }

    public List<pomodorosessionDTO.Response> getSessionsByTask(Long userId, Long taskId) {
        // Kiểm tra xem Task có tồn tại không
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        // Kiểm tra Task có thuộc User không
        if (!task.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Task doesn't belong to user");
        }

        // Lấy danh sách Pomodoro theo TaskId
        List<Pomodoro> pomodoros = pomodoroRepo.findByTaskId(taskId);

        // Lấy danh sách PomodoroSession từ danh sách Pomodoro
        return pomodoros.stream()
                .flatMap(pomodoro -> pomodoro.getSessions().stream())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Lấy danh sách PomodoroSession từ một User
    public List<pomodorosessionDTO.Response> getSessionsByUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return pomodoroSessionRepo.findByPomodoroUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
