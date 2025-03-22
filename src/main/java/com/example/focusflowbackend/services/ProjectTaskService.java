package com.example.focusflowbackend.services;

import com.example.focusflowbackend.models.ProjectTask;
import com.example.focusflowbackend.repository.ProjectTaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepo projectTaskRepository;

    public List<ProjectTask> getTasksByProjectId(Long projectId) {
        return projectTaskRepository.findByProjectId(projectId);
    }

    public ProjectTask addTaskToProject(ProjectTask projectTask) {
        return projectTaskRepository.save(projectTask);
    }

    public void removeProjectTask(Long id) {
        projectTaskRepository.deleteById(id);
    }
}
