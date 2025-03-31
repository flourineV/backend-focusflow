package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.task.taskDTO;
import com.example.focusflowbackend.models.*;
import com.example.focusflowbackend.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectTaskRepo projectTaskRepo;

    @Autowired
    private UserAccountRepo userRepo;

    @Autowired
    private TaskTagRepo taskTagRepo;

    @Autowired
    private TagRepo tagRepo;

    // Tạo task (có thể gắn vào project nếu có projectId)
    public taskDTO.Response createTask(Long userId, taskDTO.CreateRequest request, Long projectId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Task task = Task.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(request.getStatus())
                .priority(request.getPriority())
                .repeatStyle(request.getRepeatStyle())
                .reminderDaysBefore(request.getReminderDaysBefore())
                .isCompleted(false)
                .isDeleted(false)
                .build();

        Task savedTask = taskRepo.save(task);

        if (projectId != null) {
            Project project = projectRepo.findById(projectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
            Project_Task pt = new Project_Task();
            pt.setProject(project);
            pt.setTask(savedTask);
            projectTaskRepo.save(pt);
        }

        return mapToDTO(savedTask);
    }

    // Gắn tag cho task
    public void addTagsToTask(Long taskId, List<Long> tagIds) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        for (Long tagId : tagIds) {
            Tag tag = tagRepo.findById(tagId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
            Task_Tags taskTag = new Task_Tags();
            taskTag.setTask(task);
            taskTag.setTag(tag);
            taskTagRepo.save(taskTag);
        }
    }

    public List<taskDTO.Response> getTasksByUser(Long userId) {
        return taskRepo.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<taskDTO.Response> getTasksByProject(Long projectId) {
        return taskRepo.findTasksByProjectId(projectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public taskDTO.Response getTask(Long taskId) {
        return mapToDTO(taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")));
    }

    public List<taskDTO.Response> getTasksOverdue(Long userId) {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Lấy tất cả task của user và lọc những task đã quá hạn
        return taskRepo.findByUserId(userId).stream()
                .filter(task
                        -> task.getDueDate() != null
                && task.getDueDate().isBefore(now)
                && !task.isCompleted()
                )
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public taskDTO.Response updateStatus(Long taskId, taskDTO.UpdateStatus dto) {
        Task task = getEntity(taskId);
        task.setStatus(dto.getStatus());
        return mapToDTO(taskRepo.save(task));
    }

    public taskDTO.Response updatePriority(Long taskId, taskDTO.UpdatePriority dto) {
        Task task = getEntity(taskId);
        task.setPriority(dto.getPriority());
        return mapToDTO(taskRepo.save(task));
    }

    public taskDTO.Response updateCompletion(Long taskId, taskDTO.UpdateCompletion dto) {
        Task task = getEntity(taskId);
        task.setCompleted(dto.isCompleted());
        return mapToDTO(taskRepo.save(task));
    }

    public taskDTO.Response updateTitle(Long taskId, taskDTO.UpdateTitle dto) {
        Task task = getEntity(taskId);
        task.setTitle(dto.getTitle());
        return mapToDTO(taskRepo.save(task));
    }

    public taskDTO.Response updateDescription(Long taskId, taskDTO.UpdateDescription dto) {
        Task task = getEntity(taskId);
        task.setDescription(dto.getDescription());
        return mapToDTO(taskRepo.save(task));
    }

    public taskDTO.Response updateDueDate(Long taskId, taskDTO.UpdateDueDate dto) {
        Task task = getEntity(taskId);
        task.setDueDate(dto.getDueDate());
        return mapToDTO(taskRepo.save(task));
    }

    public taskDTO.Response updateRepeatStyle(Long taskId, taskDTO.UpdateRepeatStyle dto) {
        Task task = getEntity(taskId);
        task.setRepeatStyle(dto.getRepeatStyle());
        return mapToDTO(taskRepo.save(task));
    }

    public taskDTO.Response updateReminder(Long taskId, taskDTO.UpdateReminder dto) {
        Task task = getEntity(taskId);
        task.setReminderDaysBefore(dto.getReminderDaysBefore());
        return mapToDTO(taskRepo.save(task));
    }

    public void deleteTask(Long taskId) {
        if (!taskRepo.existsById(taskId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        taskRepo.deleteById(taskId);
    }

    public void moveTaskToProject(Long taskId, Long newProjectId) {
        Task task = getEntity(taskId);

        // Xóa tất cả liên kết cũ trong project_task
        List<Project_Task> existingLinks = projectTaskRepo.findByTask(task);
        projectTaskRepo.deleteAll(existingLinks);

        // Nếu có project mới, gán lại
        if (newProjectId != null) {
            Project project = projectRepo.findById(newProjectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

            Project_Task pt = new Project_Task();
            pt.setProject(project);
            pt.setTask(task);
            projectTaskRepo.save(pt);
        }
    }

    private Task getEntity(Long taskId) {
        return taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private taskDTO.Response mapToDTO(Task task) {
        Long projectId = projectTaskRepo.findByTask(task).stream()
                .map(pt -> pt.getProject().getId())
                .findFirst().orElse(null);

        List<Long> tagIds = taskTagRepo.findByTask(task).stream()
                .map(t -> t.getTag().getId())
                .collect(Collectors.toList());

        return taskDTO.Response.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .priority(String.valueOf(task.getPriority()))
                .repeatStyle(task.getRepeatStyle())
                .reminderDaysBefore(task.getReminderDaysBefore())
                .isCompleted(task.isCompleted())
                .projectId(projectId)
                .tagIds(tagIds)
                .build();
    }

    public boolean isUserOwnerOfTask(Long userId, Long taskId) {
        // Lấy task theo ID
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        // Kiểm tra xem task có thuộc về người dùng không
        return task.getUser().getId().equals(userId);
    }
}
