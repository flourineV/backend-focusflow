package com.example.focusflowbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.example.focusflowbackend.dto.task.taskDTO;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Cache task theo ID
    public void cacheTask(Long taskId, taskDTO.Response task) {
        String key = "task:" + taskId;
        redisTemplate.opsForValue().set(key, task, 60, TimeUnit.MINUTES); // Cache trong 60 phút
    }

    // Lấy task từ cache
    public taskDTO.Response getTaskFromCache(Long taskId) {
        String key = "task:" + taskId;
        return (taskDTO.Response) redisTemplate.opsForValue().get(key);
    }

    // Xóa task khỏi cache
    public void removeTaskFromCache(Long taskId) {
        String key = "task:" + taskId;
        redisTemplate.delete(key);
    }

    // Cache danh sách task của user
    public void cacheUserTasks(Long userId, List<taskDTO.Response> tasks) {
        String key = "userTasks:" + userId;
        redisTemplate.delete(key); // Xóa key cũ nếu có
        redisTemplate.opsForList().rightPushAll(key, tasks.toArray()); 
        redisTemplate.expire(key, 60, TimeUnit.MINUTES); // Cache trong 60 phút
    }

    // Lấy danh sách task của user từ cache
    @SuppressWarnings("unchecked")
    public List<taskDTO.Response> getUserTasksFromCache(Long userId) {
        String key = "userTasks:" + userId;
        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
        if (objects == null || objects.isEmpty()) {
            return null;
        }
        return objects.stream()
                .map(obj -> (taskDTO.Response) obj)
                .collect(Collectors.toList());
    }

    // Xóa danh sách task khỏi cache
    public void removeUserTasksFromCache(Long userId) {
        String key = "userTasks:" + userId;
        redisTemplate.delete(key);
    }

    // Cache task bằng cách sử dụng hash để lưu trữ task cho từng trường
    public void cacheTaskWithHash(Long taskId, taskDTO.Response task) {
        String key = "taskHash:" + taskId;
        redisTemplate.opsForHash().put(key, "task", task);
        redisTemplate.expire(key, 60, TimeUnit.MINUTES); // Cache trong 60 phút
    }

    // Lấy task từ cache dưới dạng hash
    public taskDTO.Response getTaskFromHashCache(Long taskId) {
        String key = "taskHash:" + taskId;
        return (taskDTO.Response) redisTemplate.opsForHash().get(key, "task");
    }

    // Xóa task từ cache dạng hash
    public void removeTaskFromHashCache(Long taskId) {
        String key = "taskHash:" + taskId;
        redisTemplate.opsForHash().delete(key, "task");
    }
}
