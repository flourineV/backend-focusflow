package com.example.focusflowbackend.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.concurrent.TimeUnit;

@Service
public class UserActivityService {

    private final RedisTemplate<String, String> redisTemplate;

    public UserActivityService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void trackUser(Long userId) {
        String dateKey = "dau:" + LocalDate.now();
        String monthKey = "mau:" + YearMonth.now();
        String yearKey = "yau:" + Year.now();

        String idStr = String.valueOf(userId);

        redisTemplate.opsForSet().add(dateKey, idStr);
        redisTemplate.opsForSet().add(monthKey, idStr);
        redisTemplate.opsForSet().add(yearKey, idStr);

        redisTemplate.expire(dateKey, 2, TimeUnit.DAYS);
        redisTemplate.expire(monthKey, 35, TimeUnit.DAYS); // hoặc 60
        redisTemplate.expire(yearKey, 400, TimeUnit.DAYS); // tuỳ
    }

    // Lấy DAU (Active User theo ngày cụ thể)
    public long getDAU(LocalDate date) {
        String key = "dau:" + date;
        return redisTemplate.opsForSet().size(key);
    }

    // Lấy MAU (Active User theo tháng cụ thể)
    public long getMAU(YearMonth yearMonth) {
        String key = "mau:" + yearMonth;
        return redisTemplate.opsForSet().size(key);
    }

    // Lấy YAU (Active User theo năm cụ thể)
    public long getYAU(Year year) {
        String key = "yau:" + year;
        return redisTemplate.opsForSet().size(key);
    }
}
