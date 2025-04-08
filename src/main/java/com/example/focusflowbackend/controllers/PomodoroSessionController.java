package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.pomodorosession.pomodorosessionDTO;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import com.example.focusflowbackend.services.PomodoroSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pomodoro-sessions")
@RequiredArgsConstructor
public class PomodoroSessionController {

    private final PomodoroSessionService pomodoroSessionService;
    private final AuthorizationUtils authUtils;

    // Create new session in pomo
    @PostMapping("/{pomodoroId}")
    public ResponseEntity<pomodorosessionDTO.Response> createSession(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId,
            @PathVariable Long pomodoroId,
            @RequestBody pomodorosessionDTO.CreateRequest request) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        return ResponseEntity.ok(pomodoroSessionService.createSession(userId, pomodoroId, request));
    }

    // Get LIST of sessions from Pomo
    @GetMapping("/{pomodoroId}")
    public ResponseEntity<List<pomodorosessionDTO.Response>> getSessionsByPomodoroId(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId,
            @PathVariable Long pomodoroId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        return ResponseEntity.ok(pomodoroSessionService.getSessionsByPomodoroId(userId, pomodoroId));
    }

    // Delete session
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId,
            @PathVariable Long sessionId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        pomodoroSessionService.deleteSession(userId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
