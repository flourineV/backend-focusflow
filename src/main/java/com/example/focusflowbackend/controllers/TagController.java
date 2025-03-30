package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.tag.tagDTO;
import com.example.focusflowbackend.services.TagService;
import com.example.focusflowbackend.services.TaskService;
import com.example.focusflowbackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
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
    private final JwtUtil jwtUtil;

    @PostMapping("/task/{taskId}")
    public ResponseEntity<?> addTagsToTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @RequestBody @Valid List<String> tagNames) {

        Long tokenUserId = jwtUtil.extractUserId(token);

        boolean isOwner = taskService.isUserOwnerOfTask(tokenUserId, taskId);
        if (!isOwner) {
            return ResponseEntity.status(403).body("Access denied: User is not the owner of the task");
        }

        // Chuyển từ tagNames -> tagIds
        List<Long> tagIds = tagService.getOrCreateTags(tagNames);

        tagService.addTagsToTask(taskId, tagIds);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<tagDTO.Response>> getTagsByTask(@PathVariable Long taskId) {
        List<tagDTO.Response> tags = tagService.getTagsByTask(taskId);
        return ResponseEntity.ok(tags);
    }

    @DeleteMapping("/task/{taskId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Long taskId,
            @PathVariable Long tagId) {

        Long tokenUserId = jwtUtil.extractUserId(token);
        if (!taskService.isUserOwnerOfTask(tokenUserId, taskId)) {
            return ResponseEntity.status(403).build();
        }

        tagService.removeTagFromTask(taskId, tagId);
        return ResponseEntity.noContent().build();
    }
}
