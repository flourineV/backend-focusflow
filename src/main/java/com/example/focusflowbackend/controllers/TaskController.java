package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.task.taskDTO;
import com.example.focusflowbackend.security.JwtUtil;
import com.example.focusflowbackend.services.TaskService;
import com.example.focusflowbackend.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final JwtUtil jwtUtil;

    // Tạo task không thuộc project
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestBody @Valid taskDTO.CreateRequest request) {

        Long tokenUserId = jwtUtil.extractUserId(token);
        if (!tokenUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
        }
        return ResponseEntity.ok(taskService.createTask(userId, request, null));
    }

    // Tạo task trong project
    @PostMapping("/user/{userId}/project/{projectId}")
    public ResponseEntity<?> createTaskInProject(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @PathVariable Long projectId,
            @RequestBody @Valid taskDTO.CreateRequest request) {
        Long tokenUserId = jwtUtil.extractUserId(token);
        if (!tokenUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
        }
        // Kiểm tra xem user có quyền tạo task cho project này không
        boolean isOwner = projectService.isUserOwnerOfProject(userId, projectId);
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: User is not the owner of the project");
        }
        return ResponseEntity.ok(taskService.createTask(userId, request, projectId));
    }

    // Lấy tất cả task của user
    @GetMapping("/user")
    public ResponseEntity<List<taskDTO.Response>> getTasksByUser(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token);
        List<taskDTO.Response> tasks = taskService.getTasksByUser(userId);
        return ResponseEntity.ok(tasks);
    }

    // Lấy tất cả task trong 1 project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<taskDTO.Response>> getTasksByProject(@PathVariable Long projectId) {
        List<taskDTO.Response> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    // Lấy 1 task theo ID
    @GetMapping("/{taskId}")
    public ResponseEntity<taskDTO.Response> getTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTask(taskId));
    }

    // Xóa task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    // ============ Cập nhật từng trường ============
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<taskDTO.Response> updateStatus(@PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateStatus dto) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, dto));
    }

    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<taskDTO.Response> updatePriority(@PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdatePriority dto) {
        return ResponseEntity.ok(taskService.updatePriority(taskId, dto));
    }

    @PatchMapping("/{taskId}/completion")
    public ResponseEntity<taskDTO.Response> updateCompletion(@PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateCompletion dto) {
        return ResponseEntity.ok(taskService.updateCompletion(taskId, dto));
    }

    @PatchMapping("/{taskId}/title")
    public ResponseEntity<taskDTO.Response> updateTitle(@PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateTitle dto) {
        return ResponseEntity.ok(taskService.updateTitle(taskId, dto));
    }

    @PatchMapping("/{taskId}/description")
    public ResponseEntity<taskDTO.Response> updateDescription(@PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateDescription dto) {
        return ResponseEntity.ok(taskService.updateDescription(taskId, dto));
    }

    @PatchMapping("/{taskId}/duedate")
    public ResponseEntity<taskDTO.Response> updateDueDate(@PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateDueDate dto) {
        return ResponseEntity.ok(taskService.updateDueDate(taskId, dto));
    }

    @PatchMapping("/{taskId}/repeat-style")
    public ResponseEntity<taskDTO.Response> updateRepeatStyle(@PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateRepeatStyle dto) {
        return ResponseEntity.ok(taskService.updateRepeatStyle(taskId, dto));
    }

    @PatchMapping("/{taskId}/reminder")
    public ResponseEntity<taskDTO.Response> updateReminder(@PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateReminder dto) {
        return ResponseEntity.ok(taskService.updateReminder(taskId, dto));
    }

    // Di chuyển task sang project khác
    @PatchMapping("/{taskId}/move-to-project/{projectId}")
    public ResponseEntity<Void> moveTaskToProject(@PathVariable Long taskId,
            @PathVariable Long projectId) {
        taskService.moveTaskToProject(taskId, projectId);
        return ResponseEntity.noContent().build();
    }

    // Gỡ task khỏi project
    @PatchMapping("/{taskId}/remove-from-project")
    public ResponseEntity<Void> removeFromProject(@PathVariable Long taskId) {
        taskService.moveTaskToProject(taskId, null);
        return ResponseEntity.noContent().build();
    }

    // Gắn tags cho task
    @PostMapping("/{taskId}/tags")
    public ResponseEntity<Void> addTagsToTask(@PathVariable Long taskId,
            @RequestBody List<Long> tagIds) {
        taskService.addTagsToTask(taskId, tagIds);
        return ResponseEntity.ok().build();
    }
    // Lấy tất cả task quá hạn của user

    @GetMapping("/user/overdue")
    public ResponseEntity<List<taskDTO.Response>> getTasksOverdue(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token);
        List<taskDTO.Response> overdueTasks = taskService.getTasksOverdue(userId);
        return ResponseEntity.ok(overdueTasks);
    }
}
