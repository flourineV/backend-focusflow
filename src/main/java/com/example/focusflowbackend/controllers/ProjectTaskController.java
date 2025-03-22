package com.example.focusflowbackend.controllers;

import com.example.focusflowbackend.models.ProjectTask;
import com.example.focusflowbackend.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-tasks")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @GetMapping("/project/{projectId}")
    public List<ProjectTask> getTasksByProject(@PathVariable Long projectId) {
        return projectTaskService.getTasksByProjectId(projectId);
    }

    @PostMapping
    public ProjectTask addTaskToProject(@RequestBody ProjectTask projectTask) {
        return projectTaskService.addTaskToProject(projectTask);
    }

    @DeleteMapping("/{id}")
    public void removeProjectTask(@PathVariable Long id) {
        projectTaskService.removeProjectTask(id);
    }
}
