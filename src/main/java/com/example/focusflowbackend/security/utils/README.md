# Authorization Utils

Đây là một tiện ích giúp quản lý việc kiểm tra quyền truy cập trong ứng dụng. Nó cung cấp các phương thức để:

1. Kiểm tra xem người dùng hiện tại có phải là admin hoặc chính người dùng cần truy cập
2. Tạo các loại response khi quyền truy cập bị từ chối

## Cách sử dụng

### 1. Đưa AuthorizationUtils vào controller

```java
@RestController
@RequestMapping("/api/endpoint")
@RequiredArgsConstructor  // Lombok sẽ tạo constructor
public class YourController {

    private final AuthorizationUtils authUtils;
    
    // Controller methods...
}
```

### 2. Kiểm tra quyền truy cập trong các phương thức controller

```java
@GetMapping("/user/{userId}")
public ResponseEntity<?> getUserData(
        @RequestHeader("Authorization") String token,
        @PathVariable Long userId) {
    
    // Kiểm tra xem người dùng có quyền truy cập không
    if (!authUtils.isAdminOrSameUser(token, userId)) {
        return authUtils.createForbiddenResponse();
    }
    
    // Tiếp tục xử lý khi có quyền truy cập
    return ResponseEntity.ok(userService.getUserData(userId));
}
```

### 3. Sử dụng các loại response khác nhau tùy tình huống

```java
// Trả về 403 không có body
return authUtils.createForbiddenResponse();

// Trả về 403 với ErrorResponse
return authUtils.createForbiddenErrorResponse();

// Trả về 403 với ErrorResponse tùy chỉnh
return authUtils.createForbiddenErrorResponse("Custom error message");

// Trả về 403 với MessageResponse
return authUtils.createForbiddenMessageResponse();

// Trả về 403 với String
return authUtils.createForbiddenStringResponse();

// Trả về 403 với pomodoroDTO.Response (dành riêng cho PomodoroController)
return authUtils.createForbiddenPomodoroResponse();
```

## Các phương thức kiểm tra quyền

1. `isAdminOrSameUser(token, userId)`: Kiểm tra xem người dùng có phải admin hoặc chính người dùng cần truy cập
2. `isAdmin(token)`: Kiểm tra xem người dùng có phải admin
3. `getCurrentUserId(token)`: Lấy ID của người dùng hiện tại từ token 