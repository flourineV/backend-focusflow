package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.tag.tagDTO;
import com.example.focusflowbackend.services.TagService;
import com.example.focusflowbackend.services.TaskService;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TaskService taskService;
    private final AuthorizationUtils authUtils;

    //Create new tag for tasks
    @PostMapping("/task/{taskId}")
    public ResponseEntity<?> addTagsToTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody @Valid List<String> tagNames) {

        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isTaskOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);

        if (!isTaskOwner && !isAdmin) {
            return authUtils.createForbiddenStringResponse();
        }

        List<Long> tagIds = tagService.getOrCreateTags(tagNames);

        tagService.addTagsToTask(taskId, tagIds);

        return ResponseEntity.ok().build();
    }

    //Get all tags in 1 task
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<tagDTO.Response>> getTagsByTask(@PathVariable Long taskId) {
        List<tagDTO.Response> tags = tagService.getTagsByTask(taskId);
        return ResponseEntity.ok(tags);
    }

    @DeleteMapping("/task/{taskId}/tags/{tagId}")
    public ResponseEntity<String> removeTagFromTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @PathVariable Long tagId) {

        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isTaskOwner = taskService.isTaskOwnedByUser(taskId, userId);
        boolean isAdmin = authUtils.isAdmin(token);

        if (!isTaskOwner && !isAdmin) {
            return authUtils.createForbiddenStringResponse();
        }

        tagService.removeTagFromTask(taskId, tagId);
        return ResponseEntity.ok("Tag removed successfully");
    }
}
