package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.project.projectDTO;
import com.example.focusflowbackend.models.Project;
import com.example.focusflowbackend.security.JwtUtil;
import com.example.focusflowbackend.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final JwtUtil jwtUtil;

    // Tạo project mới cho user
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createProject(@RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestBody @Valid projectDTO.CreateRequest request) {
        Long tokenUserId = jwtUtil.extractUserId(token);
        if (!tokenUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
        }
        Project project = projectService.createProject(userId, request);
        return ResponseEntity.ok(project); // vẫn trả về Project vì create dùng entity
    }

    // Lấy danh sách project theo user → Trả về List<projectDTO.Response>
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProjectsByUser(@RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            Long tokenUserId = jwtUtil.extractUserId(token);
            if (!tokenUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid user");
            }
            List<projectDTO.Response> projects = projectService.getProjectsByUser(userId);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    // Cập nhật tên → trả DTO
    @PatchMapping("/{projectId}/name")
    public ResponseEntity<projectDTO.Response> updateProjectName(@PathVariable Long projectId,
            @RequestBody @Valid projectDTO.UpdateName request) {
        return ResponseEntity.ok(projectService.updateName(projectId, request));
    }

    // Cập nhật mô tả → trả DTO
    @PatchMapping("/{projectId}/description")
    public ResponseEntity<projectDTO.Response> updateProjectDescription(@PathVariable Long projectId,
            @RequestBody @Valid projectDTO.UpdateDescription request) {
        return ResponseEntity.ok(projectService.updateDescription(projectId, request));
    }

    // Xoá project
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
