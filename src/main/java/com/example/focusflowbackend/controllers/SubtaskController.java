package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.subtask.subtaskDTO;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import com.example.focusflowbackend.services.SubtaskService;
import com.example.focusflowbackend.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/subtasks")
@RequiredArgsConstructor
public class SubtaskController {

    private final SubtaskService subtaskService;
    private final TaskService taskService;
    private final AuthorizationUtils authUtils;

    // Create new subtasks
    @PostMapping
    public ResponseEntity<subtaskDTO.Response> createSubtask(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid subtaskDTO.CreateRequest request) {

        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isTaskOwner = taskService.isTaskOwnedByUser(request.getTaskId(), userId);
        boolean isAdmin = authUtils.isAdmin(token);

        if (!isTaskOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(subtaskService.createSubtask(request));
    }

    // Get All Subtasks in 1 Task
    @GetMapping("/{taskId}")
    public ResponseEntity<List<subtaskDTO.Response>> getSubtasksByTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId) {

        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isTaskOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);

        if (!isTaskOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(subtaskService.getSubtasksByTask(taskId));
    }

    //Update completed
    @PatchMapping("/{subtaskId}/completed")
    public ResponseEntity<subtaskDTO.Response> updateSubtaskCompleted(
            @RequestHeader("Authorization") String token,
            @PathVariable Long subtaskId,
            @RequestBody @Valid subtaskDTO.UpdateCompletion request) {

        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long taskId = subtaskService.getTaskIdBySubtaskId(subtaskId);
        boolean isTaskOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);

        if (!isTaskOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(subtaskService.updateSubtaskCompleted(subtaskId, request));
    }

    //Update title
    @PatchMapping("/{subtaskId}/title")
    public ResponseEntity<subtaskDTO.Response> updateSubtaskTitle(
            @RequestHeader("Authorization") String token,
            @PathVariable Long subtaskId,
            @RequestBody @Valid subtaskDTO.UpdateTitle request) {

        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long taskId = subtaskService.getTaskIdBySubtaskId(subtaskId);
        boolean isTaskOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);

        if (!isTaskOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(subtaskService.updateSubtaskTitle(subtaskId, request));
    }

    // Delete subtasks
    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<Void> deleteSubtask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long subtaskId) {

        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long taskId = subtaskService.getTaskIdBySubtaskId(subtaskId);
        boolean isTaskOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);

        if (!isTaskOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        subtaskService.deleteSubtask(subtaskId);
        return ResponseEntity.noContent().build();
    }
}
