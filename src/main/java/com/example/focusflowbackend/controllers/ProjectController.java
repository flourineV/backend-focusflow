package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.dto.project.projectDTO;
import com.example.focusflowbackend.models.Project;
import com.example.focusflowbackend.security.utils.AuthorizationUtils;
import com.example.focusflowbackend.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final AuthorizationUtils authUtils;

    // Create new Project
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createProject(@RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestBody @Valid projectDTO.CreateRequest request) {
        if (!authUtils.isAdminOrSameUser(token, userId)) {
            return authUtils.createForbiddenStringResponse();
        }
        Project project = projectService.createProject(userId, request);
        return ResponseEntity.ok(project); // vẫn trả về Project vì create dùng entity
    }

    // Get LIST of projects by users
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProjectsByUser(@RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!authUtils.isAdminOrSameUser(token, userId)) {
                return authUtils.createForbiddenStringResponse();
            }
            List<projectDTO.Response> projects = projectService.getProjectsByUser(userId);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    // Update name
    @PatchMapping("/{projectId}/name")
    public ResponseEntity<projectDTO.Response> updateProjectName(
            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId,
            @RequestBody @Valid projectDTO.UpdateName request) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = projectService.isUserOwnerOfProject(userId, projectId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(projectService.updateName(projectId, request));
    }

    // Update description
    @PatchMapping("/{projectId}/description")
    public ResponseEntity<projectDTO.Response> updateProjectDescription(
            @RequestHeader("Authorization") String token,
            @PathVariable Long projectId,
            @RequestBody @Valid projectDTO.UpdateDescription request) {
        Long userId = authUtils.getCurrentUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isOwner = projectService.isUserOwnerOfProject(userId, projectId);
        boolean isAdmin = authUtils.isAdmin(token);
        if (!isOwner && !isAdmin) {
            return authUtils.createForbiddenResponse();
        }

        return ResponseEntity.ok(projectService.updateDescription(projectId, request));
    }

    // Delete projects
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
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

        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
