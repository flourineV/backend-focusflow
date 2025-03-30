package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.subtask.subtaskDTO;
import com.example.focusflowbackend.models.Subtask;
import com.example.focusflowbackend.models.Task;
import com.example.focusflowbackend.repository.SubtaskRepo;
import com.example.focusflowbackend.repository.TaskRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubtaskService {

    private final SubtaskRepo subtaskRepo;
    private final TaskRepo taskRepo;

    // Tạo Subtask mới
    public subtaskDTO.Response createSubtask(subtaskDTO.CreateRequest request) {
        Task task = taskRepo.findById(request.getTaskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        Subtask subtask = Subtask.builder()
                .task(task)
                .title(request.getTitle())
                .isCompleted(false)
                .build();

        subtask = subtaskRepo.save(subtask);
        return mapToDTO(subtask);
    }

    // Lấy tất cả subtasks của một task
    public List<subtaskDTO.Response> getSubtasksByTask(Long taskId) {
        return subtaskRepo.findByTaskId(taskId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public subtaskDTO.Response updateSubtaskCompleted(Long subtaskId, subtaskDTO.UpdateCompletion request) {
        Subtask subtask = subtaskRepo.findById(subtaskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask not found"));

        subtask.setCompleted(request.isCompleted()); // hoặc getIsCompleted() tùy lombok version
        subtaskRepo.save(subtask);

        return mapToDTO(subtask); // ✅ dùng hàm map có sẵn
    }

    public subtaskDTO.Response updateSubtaskTitle(Long subtaskId, subtaskDTO.UpdateTitle request) {
        Subtask subtask = subtaskRepo.findById(subtaskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask not found"));

        subtask.setTitle(request.getTitle());
        subtaskRepo.save(subtask);

        return mapToDTO(subtask); // ✅ dùng hàm map có sẵn
    }

    // Xoá subtask
    @Transactional
    public void deleteSubtask(Long subtaskId) {
        if (!subtaskRepo.existsById(subtaskId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask not found");
        }
        subtaskRepo.deleteById(subtaskId);
    }

    // Map Entity -> DTO
    private subtaskDTO.Response mapToDTO(Subtask subtask) {
        return subtaskDTO.Response.builder()
                .id(subtask.getId())
                .title(subtask.getTitle())
                .isCompleted(subtask.isCompleted())
                .taskId(subtask.getTask().getId())
                .build();
    }

    public Long getTaskIdBySubtaskId(Long subtaskId) {
        Subtask subtask = subtaskRepo.findById(subtaskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask not found"));
        return subtask.getTask().getId();
    }

}
