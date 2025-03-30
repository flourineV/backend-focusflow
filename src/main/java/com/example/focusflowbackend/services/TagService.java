package com.example.focusflowbackend.services;

import com.example.focusflowbackend.dto.tag.tagDTO;
import com.example.focusflowbackend.models.Tag;
import com.example.focusflowbackend.models.Task;
import com.example.focusflowbackend.models.Task_Tags;
import com.example.focusflowbackend.repository.TagRepo;
import com.example.focusflowbackend.repository.TaskRepo;
import com.example.focusflowbackend.repository.TaskTagRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepo tagRepo;
    private final TaskRepo taskRepo;
    private final TaskTagRepo taskTagRepo;

    // Tạo mới tag
    public tagDTO.Response createTag(String tagName) {
        Tag tag = tagRepo.findByName(tagName)
                .orElseGet(() -> tagRepo.save(Tag.builder().name(tagName).build()));
        return mapToDTO(tag);
    }

    public void addTagsToTask(Long taskId, List<Long> tagIds) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        for (Long tagId : tagIds) {
            Tag tag = tagRepo.findById(tagId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
            Task_Tags taskTag = Task_Tags.builder()
                    .task(task)
                    .tag(tag)
                    .build();
            taskTagRepo.save(taskTag);
        }
    }

    // Lấy tất cả tag của task
    public List<tagDTO.Response> getTagsByTask(Long taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return taskTagRepo.findByTask(task).stream()
                .map(taskTag -> mapToDTO(taskTag.getTag()))
                .collect(Collectors.toList());
    }

    // Xoá tag khỏi task
    @Transactional
    public void removeTagFromTask(Long taskId, Long tagId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        Tag tag = tagRepo.findById(tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
        Task_Tags taskTag = taskTagRepo.findByTask(task).stream()
                .filter(t -> t.getTag().equals(tag))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not associated with this task"));
        taskTagRepo.delete(taskTag);
    }

    private tagDTO.Response mapToDTO(Tag tag) {
        return tagDTO.Response.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    public List<Long> getOrCreateTags(List<String> tagNames) {
        return tagNames.stream()
                .map(name -> tagRepo.findByName(name)
                .orElseGet(() -> tagRepo.save(Tag.builder().name(name).build()))
                )
                .map(Tag::getId)
                .collect(Collectors.toList());
    }

}
