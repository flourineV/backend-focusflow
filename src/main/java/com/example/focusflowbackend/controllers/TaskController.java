package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.task.taskDTO;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
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
    private final AuthorizationUtils authUtils;

    // Create new task (not belong to Project)
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestBody @Valid taskDTO.CreateRequest request) {

        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenStringResponse();
        }
        return ResponseEntity.ok(taskService.createTask(userId, request, null));
    }

    // Create new task (belong to Project)
    @PostMapping("/user/{userId}/project/{projectId}")
    public ResponseEntity<?> createTaskInProject(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @PathVariable Long projectId,
            @RequestBody @Valid taskDTO.CreateRequest request) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenStringResponse();
        }
        boolean isOwner = projectService.isUserOwnerOfProject(userId, projectId);
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: User is not the owner of the project");
        }
        return ResponseEntity.ok(taskService.createTask(userId, request, projectId));
    }

    //Get all completed task today
    @GetMapping("/user/{userId}/completed-today")
    public ResponseEntity<Long> getCompletedTaskCountToday(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        long count = taskService.countTasksCompletedToday(userId);
        return ResponseEntity.ok(count);
    }

    // Get all task from user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<taskDTO.Response>> getTasksByUser(
            @RequestHeader("Authorization") String token) {
        Long userId = authUtils.getCurrentUserId(token);
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        List<taskDTO.Response> tasks = taskService.getTasksByUser(userId);
        return ResponseEntity.ok(tasks);
    }

    // Get all task from user sorted by priority
    @GetMapping("/user/{userId}/sorted-by-priority")
    public ResponseEntity<List<taskDTO.Response>> getTasksByUserSortedByPriority(
            @RequestHeader("Authorization") String token) {
        Long userId = authUtils.getCurrentUserId(token);
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenResponse();
        }
        List<taskDTO.Response> tasks = taskService.getTasksByUserSortedByPriority(userId);
        return ResponseEntity.ok(tasks);
    }

    // Get all task in project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<taskDTO.Response>> getTasksByProject(
            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isOwner = projectService.isUserOwnerOfProject(userId, projectId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        List<taskDTO.Response> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    // get 1 task
    @GetMapping("/{taskId}")
    public ResponseEntity<taskDTO.Response> getTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        taskDTO.Response task = taskService.getTask(taskId);
        boolean isOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }
        return ResponseEntity.ok(task);
    }

    // Delete task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    //Update priority
    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<taskDTO.Response> updatePriority(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdatePriority dto) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(taskService.updatePriority(taskId, dto));
    }

    //Update completion
    @PatchMapping("/{taskId}/completion")
    public ResponseEntity<taskDTO.Response> updateCompletion(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateCompletion dto) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(taskService.updateCompletion(taskId, dto));
    }

    //Update title
    @PatchMapping("/{taskId}/title")
    public ResponseEntity<taskDTO.Response> updateTitle(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateTitle dto) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(taskService.updateTitle(taskId, dto));
    }

    //Update description
    @PatchMapping("/{taskId}/description")
    public ResponseEntity<taskDTO.Response> updateDescription(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateDescription dto) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(taskService.updateDescription(taskId, dto));
    }

    //Update duedate
    @PatchMapping("/{taskId}/duedate")
    public ResponseEntity<taskDTO.Response> updateDueDate(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateDueDate dto) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(taskService.updateDueDate(taskId, dto));
    }

    //Update repeat-style
    @PatchMapping("/{taskId}/repeat-style")
    public ResponseEntity<taskDTO.Response> updateRepeatStyle(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateRepeatStyle dto) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(taskService.updateRepeatStyle(taskId, dto));
    }

    //Update reminder
    @PatchMapping("/{taskId}/reminder")
    public ResponseEntity<taskDTO.Response> updateReminder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody @Valid taskDTO.UpdateReminder dto) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(taskService.updateReminder(taskId, dto));
    }

    // Move task to another project
    @PatchMapping("/{taskId}/move-to-project/{projectId}")
    public ResponseEntity<Void> moveTaskToProject(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @PathVariable Long projectId) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isTaskOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isProjectOwner = projectService.isUserOwnerOfProject(userId, projectId);
        boolean isAdmin = authUtils.isAdmin(token);

        if ((!isTaskOwner || !isProjectOwner) && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        taskService.moveTaskToProject(taskId, projectId);
        return ResponseEntity.noContent().build();
    }

    // Delete task from project
    @PatchMapping("/{taskId}/remove-from-project")
    public ResponseEntity<Void> removeFromProject(
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

        taskService.moveTaskToProject(taskId, null);
        return ResponseEntity.noContent().build();
    }

    // Belong tags to tasks
    @PostMapping("/{taskId}/tags")
    public ResponseEntity<Void> addTagsToTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody List<Long> tagIds) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isTaskOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);

        if (!isTaskOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        taskService.addTagsToTask(taskId, tagIds);
        return ResponseEntity.ok().build();
    }

    // Get all expired task by user
    @GetMapping("/user/overdue")
    public ResponseEntity<List<taskDTO.Response>> getTasksOverdue(
            @RequestHeader("Authorization") String token) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<taskDTO.Response> overdueTasks = taskService.getTasksOverdue(userId);
        return ResponseEntity.ok(overdueTasks);
    }
}
