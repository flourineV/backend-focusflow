package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.project.projectDTO;
import com.example.focusflowbackend.models.Project;
import com.example.focusflowbackend.models.User;
import com.example.focusflowbackend.repository.ProjectRepo;
import com.example.focusflowbackend.repository.ProjectTaskRepo;
import com.example.focusflowbackend.repository.UserAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private UserAccountRepo userRepo;
    
    @Autowired
    private ProjectTaskRepo projectTaskRepo;

    // Tạo project mới cho user
    public Project createProject(Long userId, projectDTO.CreateRequest dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Project project = Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .user(user)
                .build();

        return projectRepo.save(project);
    }

    // Lấy danh sách project theo user → trả DTO
    public List<projectDTO.Response> getProjectsByUser(Long userId) {
        return projectRepo.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy 1 project → trả DTO
    public projectDTO.Response getProject(Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        return mapToDTO(project);
    }

    // Cập nhật tên
    public projectDTO.Response updateName(Long projectId, projectDTO.UpdateName dto) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        project.setName(dto.getName());
        return mapToDTO(projectRepo.save(project));
    }

    // Cập nhật mô tả
    public projectDTO.Response updateDescription(Long projectId, projectDTO.UpdateDescription dto) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        project.setDescription(dto.getDescription());
        return mapToDTO(projectRepo.save(project));
    }

    // Xoá project
    public void deleteProject(Long projectId) {
        if (!projectRepo.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        projectRepo.deleteById(projectId);
    }

    // Mapping Project -> projectDTO.Response
    private projectDTO.Response mapToDTO(Project project) {
        // Đếm số lượng task trong project
        int taskCount = projectTaskRepo.findByProjectId(project.getId()).size();
        
        return projectDTO.Response.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .taskCount(taskCount)
                .build();
    }

    public boolean isUserOwnerOfProject(Long userId, Long projectId) {
        // Lấy project theo ID
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        // Kiểm tra xem userId có phải là chủ sở hữu của project không
        return project.getUser().getId().equals(userId);
    }

}
