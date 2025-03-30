package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.subtask.subtaskDTO;
import com.example.focusflowbackend.security.JwtUtil;
import com.example.focusflowbackend.services.SubtaskService;
import com.example.focusflowbackend.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subtasks")
@RequiredArgsConstructor
public class SubtaskController {

    private final SubtaskService subtaskService;
    private final TaskService taskService;
    private final JwtUtil jwtUtil;

    // ✅ Tạo Subtask mới
    @PostMapping
    public ResponseEntity<subtaskDTO.Response> createSubtask(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid subtaskDTO.CreateRequest request) {

        Long userId = jwtUtil.extractUserId(token);
        if (!taskService.isUserOwnerOfTask(userId, request.getTaskId())) {
            return ResponseEntity.status(403).body(null);
        }

        return ResponseEntity.ok(subtaskService.createSubtask(request));
    }

    // ✅ Lấy tất cả Subtasks của một Task
    @GetMapping("/{taskId}")
    public ResponseEntity<List<subtaskDTO.Response>> getSubtasksByTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId) {

        Long userId = jwtUtil.extractUserId(token);
        if (!taskService.isUserOwnerOfTask(userId, taskId)) {
            return ResponseEntity.status(403).body(null);
        }

        return ResponseEntity.ok(subtaskService.getSubtasksByTask(taskId));
    }

    @PatchMapping("/{subtaskId}/completed")
    public ResponseEntity<subtaskDTO.Response> updateSubtaskCompleted(
            @RequestHeader("Authorization") String token,
            @PathVariable Long subtaskId,
            @RequestBody @Valid subtaskDTO.UpdateCompletion request) {

        Long userId = jwtUtil.extractUserId(token);
        Long taskId = subtaskService.getTaskIdBySubtaskId(subtaskId);

        if (!taskService.isUserOwnerOfTask(userId, taskId)) {
            return ResponseEntity.status(403).body(null);
        }

        return ResponseEntity.ok(subtaskService.updateSubtaskCompleted(subtaskId, request));
    }

    @PatchMapping("/{subtaskId}/title")
    public ResponseEntity<subtaskDTO.Response> updateSubtaskTitle(
            @RequestHeader("Authorization") String token,
            @PathVariable Long subtaskId,
            @RequestBody @Valid subtaskDTO.UpdateTitle request) {

        Long userId = jwtUtil.extractUserId(token);
        Long taskId = subtaskService.getTaskIdBySubtaskId(subtaskId);

        if (!taskService.isUserOwnerOfTask(userId, taskId)) {
            return ResponseEntity.status(403).body(null);
        }

        return ResponseEntity.ok(subtaskService.updateSubtaskTitle(subtaskId, request));
    }

    // ✅ Xoá Subtask
    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<Void> deleteSubtask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long subtaskId) {

        Long userId = jwtUtil.extractUserId(token);
        Long taskId = subtaskService.getTaskIdBySubtaskId(subtaskId);

        if (!taskService.isUserOwnerOfTask(userId, taskId)) {
            return ResponseEntity.status(403).build();
        }

        subtaskService.deleteSubtask(subtaskId);
        return ResponseEntity.noContent().build();
    }
}
