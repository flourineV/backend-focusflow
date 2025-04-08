package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import com.example.focusflowbackend.dto.pomodoro.pomodoroDTO;
import com.example.focusflowbackend.dto.pomodorosession.pomodorosessionDTO;
import com.example.focusflowbackend.services.PomodoroService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pomodoro")
@RequiredArgsConstructor
public class PomodoroController {

    private final PomodoroService pomodoroService;
    private final AuthorizationUtils authUtils;

    // Create new Pomo
    @PostMapping("user/{userId}")
    public ResponseEntity<pomodoroDTO.Response> createPomodoro(@RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestBody pomodoroDTO.CreateRequest request) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenPomodoroResponse();
        }
        return ResponseEntity.ok(pomodoroService.createPomodoro(userId, request));
    }

    // GET all Pomo 
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<pomodoroDTO.Response>> getPomodorosByUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        return ResponseEntity.ok(pomodoroService.getPomodorosByUser(userId));
    }

    // GET each Pomo
    @GetMapping("/{pomodoroId}")
    public ResponseEntity<pomodoroDTO.Response> getPomodoroById(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId,
            @PathVariable Long pomodoroId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenPomodoroResponse();
        }
        return ResponseEntity.ok(pomodoroService.getPomodoroById(userId, pomodoroId));
    }

    // Delete Pomo
    @DeleteMapping("/{pomodoroId}")
    public ResponseEntity<Void> softDeletePomodoro(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId,
            @PathVariable Long pomodoroId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        pomodoroService.softDeletePomodoro(userId, pomodoroId);
        return ResponseEntity.noContent().build();
    }

    // Get Pomo by Date
    @GetMapping("/user/{userId}/date")
    public ResponseEntity<List<pomodoroDTO.Response>> getPomodorosByDate(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestParam LocalDate date) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        return ResponseEntity.ok(pomodoroService.getPomodorosByDate(userId, date));
    }

    //Get Pomo Today
    @GetMapping("/user/{userId}/today")
    public ResponseEntity<List<pomodoroDTO.Response>> getTodayPomodoros(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        LocalDate today = LocalDate.now();
        return ResponseEntity.ok(pomodoroService.getPomodorosByDate(userId, today));
    }

    // Add session to Pomodoro
    @PostMapping("/{pomodoroId}/sessions")
    public ResponseEntity<pomodoroDTO.Response> addSessionToPomodoro(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId,
            @PathVariable Long pomodoroId,
            @RequestBody pomodorosessionDTO.CreateRequest request) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenPomodoroResponse();
        }
        return ResponseEntity.ok(pomodoroService.addSessionToPomodoro(userId, pomodoroId, request));
    }

    // Update note Pomodoro
    @PatchMapping("/{pomodoroId}/note")
    public ResponseEntity<pomodoroDTO.Response> updateNote(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId,
            @PathVariable Long pomodoroId,
            @RequestBody pomodoroDTO.UpdateNote request) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenPomodoroResponse();
        }
        return ResponseEntity.ok(pomodoroService.updateNote(userId, pomodoroId, request));
    }

    // Update isDeleted
    @PatchMapping("/{pomodoroId}/isDeleted")
    public ResponseEntity<pomodoroDTO.Response> updateIsDeleted(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId,
            @PathVariable Long pomodoroId,
            @RequestBody pomodoroDTO.UpdateIsDeleted request) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenPomodoroResponse();
        }
        return ResponseEntity.ok(pomodoroService.updateIsDeleted(userId, pomodoroId, request));
    }
}
