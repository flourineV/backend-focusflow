# FocusFlow Backend API Documentation

Backend API cho ứng dụng FocusFlow - Ứng dụng quản lý thời gian và tăng năng suất.

## Mục lục

- [Authentication](#authentication)
- [User Profile](#user-profile)
- [Projects](#projects)
- [Tasks](#tasks)
- [Subtasks](#subtasks)
- [Tags](#tags)
- [Pomodoro](#pomodoro)
- [Pomodoro Sessions](#pomodoro-sessions)
- [Streak](#streak)
- [Settings](#settings)

## Authentication

### Register

- **URL**: `/api/register`
- **Method**: `POST`
- **Authentication**: Không cần
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password"
  }
  ```
- **Response**:
  ```json
  {
    "token": "jwt_token",
    "refreshToken": "refresh_token"
  }
  ```

### Login

- **URL**: `/api/login`
- **Method**: `POST`
- **Authentication**: Không cần
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password"
  }
  ```
- **Response**:
  ```json
  {
    "token": "jwt_token",
    "refreshToken": "refresh_token"
  }
  ```

### Refresh Token

- **URL**: `/api/refreshtoken`
- **Method**: `POST`
- **Authentication**: Không cần
- **Request Body**:
  ```json
  {
    "refreshToken": "refresh_token"
  }
  ```
- **Response**:
  ```json
  {
    "token": "new_jwt_token",
    "refreshToken": "refresh_token"
  }
  ```

### Logout

- **URL**: `/api/logout`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Param**: `userId`
- **Response**:
  ```json
  {
    "message": "Log out successful!"
  }
  ```

### Delete User

- **URL**: `/api/users/{userId}`
- **Method**: `DELETE`
- **Authentication**: JWT Token
- **Response**:
  ```json
  "User deleted successfully"
  ```

### Forgot Password

- **URL**: `/api/forgot-password`
- **Method**: `POST`
- **Authentication**: Không cần
- **Request Body**:
  ```json
  {
    "email": "user@example.com"
  }
  ```
- **Response**:
  ```json
  {
    "message": "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn."
  }
  ```

### Reset Password

- **URL**: `/api/reset-password`
- **Method**: `POST`
- **Authentication**: Không cần
- **Request Body**:
  ```json
  {
    "token": "reset_token",
    "newPassword": "new_password"
  }
  ```
- **Response**:
  ```json
  {
    "message": "Mật khẩu đã được đặt lại thành công"
  }
  ```

## User Profile

### Get User Profile

- **URL**: `/api/user-profile/{userId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: User Profile object

### Update User Profile

- **URL**: `/api/user-profile/{userId}`
- **Method**: `PUT`
- **Authentication**: JWT Token
- **Request Body**: User Profile object với các trường cần cập nhật
- **Response**: Updated User Profile object

## Projects

### Create Project

- **URL**: `/api/projects/user/{userId}`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "name": "Project Name",
    "description": "Project Description",
    "color": "#FF5733"
  }
  ```
- **Response**: Project object

### Get Projects by User

- **URL**: `/api/projects/user/{userId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Array of Project objects

### Update Project

- **URL**: `/api/projects/{projectId}`
- **Method**: `PUT` 
- **Authentication**: JWT Token
- **Request Body**: Project object với các trường cần cập nhật
- **Response**: Updated Project object

### Delete Project

- **URL**: `/api/projects/{projectId}`
- **Method**: `DELETE`
- **Authentication**: JWT Token
- **Response**: HTTP Status 204 (No Content)

## Tasks

### Create Task (không thuộc Project)

- **URL**: `/api/tasks/user/{userId}`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "title": "Task Title",
    "description": "Task Description",
    "dueDate": "2023-12-31T23:59:59",
    "priority": "HIGH",
    "completed": false
  }
  ```
- **Response**: Task object

### Create Task (thuộc Project)

- **URL**: `/api/tasks/user/{userId}/project/{projectId}`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Body**: Giống như tạo task không thuộc project
- **Response**: Task object

### Get Tasks by User

- **URL**: `/api/tasks/user/{userId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Array of Task objects

### Get Tasks by User Sorted by Priority

- **URL**: `/api/tasks/user/{userId}/sorted-by-priority`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Array of Task objects sorted by priority

### Get Completed Task Count Today

- **URL**: `/api/tasks/user/{userId}/completed-today`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Number of completed tasks today

### Get Tasks by Project

- **URL**: `/api/tasks/project/{projectId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Array of Task objects

### Get Single Task

- **URL**: `/api/tasks/{taskId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Task object

### Delete Task

- **URL**: `/api/tasks/{taskId}`
- **Method**: `DELETE`
- **Authentication**: JWT Token
- **Response**: HTTP Status 204 (No Content)

### Update Task Priority

- **URL**: `/api/tasks/{taskId}/priority`
- **Method**: `PATCH`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "priority": "MEDIUM"
  }
  ```
- **Response**: Updated Task object

### Update Task Completion

- **URL**: `/api/tasks/{taskId}/completion`
- **Method**: `PATCH`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "completed": true
  }
  ```
- **Response**: Updated Task object

### Update Task Title

- **URL**: `/api/tasks/{taskId}/title`
- **Method**: `PATCH`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "title": "New Task Title"
  }
  ```
- **Response**: Updated Task object

## Subtasks

### Create Subtask

- **URL**: `/api/subtasks`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "title": "Subtask Title",
    "taskId": 1,
    "completed": false
  }
  ```
- **Response**: Subtask object

### Get Subtasks by Task

- **URL**: `/api/subtasks/task/{taskId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Array of Subtask objects

### Update Subtask Completion

- **URL**: `/api/subtasks/{subtaskId}/completion`
- **Method**: `PATCH`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "completed": true
  }
  ```
- **Response**: Updated Subtask object

### Delete Subtask

- **URL**: `/api/subtasks/{subtaskId}`
- **Method**: `DELETE`
- **Authentication**: JWT Token
- **Response**: HTTP Status 204 (No Content)

## Tags

### Add Tags to Task

- **URL**: `/api/tags/task/{taskId}`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Body**: Array of tag names
  ```json
  ["work", "important", "deadline"]
  ```
- **Response**: HTTP Status 200 (OK)

### Get Tags by Task

- **URL**: `/api/tags/task/{taskId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Array of Tag objects

## Pomodoro

### Create Pomodoro

- **URL**: `/api/pomodoro/user/{userId}`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "name": "Pomodoro Name",
    "description": "Pomodoro Description",
    "focusDuration": 25,
    "shortBreakDuration": 5,
    "longBreakDuration": 15,
    "rounds": 4
  }
  ```
- **Response**: Pomodoro object

### Get Pomodoros by User

- **URL**: `/api/pomodoro/user/{userId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Array of Pomodoro objects

### Get Pomodoro by ID

- **URL**: `/api/pomodoro/{pomodoroId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Pomodoro object

### Update Pomodoro

- **URL**: `/api/pomodoro/{pomodoroId}`
- **Method**: `PUT`
- **Authentication**: JWT Token
- **Request Body**: Pomodoro object với các trường cần cập nhật
- **Response**: Updated Pomodoro object

### Delete Pomodoro

- **URL**: `/api/pomodoro/{pomodoroId}`
- **Method**: `DELETE`
- **Authentication**: JWT Token
- **Response**: HTTP Status 204 (No Content)

## Pomodoro Sessions

### Create Pomodoro Session

- **URL**: `/api/pomodoro-sessions/{pomodoroId}`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Param**: `userId`
- **Request Body**:
  ```json
  {
    "startTime": "2023-12-31T10:00:00",
    "endTime": "2023-12-31T10:25:00",
    "completed": true,
    "notes": "Session notes here"
  }
  ```
- **Response**: Pomodoro Session object

### Get Sessions by Pomodoro ID

- **URL**: `/api/pomodoro-sessions/{pomodoroId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Request Param**: `userId`
- **Response**: Array of Pomodoro Session objects

### Delete Session

- **URL**: `/api/pomodoro-sessions/{sessionId}`
- **Method**: `DELETE`
- **Authentication**: JWT Token
- **Request Param**: `userId`
- **Response**: HTTP Status 204 (No Content)

## Streak

### Get Streak Records by User

- **URL**: `/api/streak-records/user/{userId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Array of Streak Record objects

### Create Streak Record

- **URL**: `/api/streak-records/user/{userId}`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "date": "2023-12-31",
    "minutes": 120
  }
  ```
- **Response**: Created Streak Record object

### Update Streak Record

- **URL**: `/api/streak-records/{recordId}`
- **Method**: `PUT`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "minutes": 150
  }
  ```
- **Response**: Updated Streak Record object

## Settings

### Get User Settings

- **URL**: `/api/settings/{userId}`
- **Method**: `GET`
- **Authentication**: JWT Token
- **Response**: Setting object containing:
  ```json
  {
    "language": "vi",
    "theme": "dark",
    "taskReminder": true,
    "notificationEnabled": true,
    "pomodoroDuration": 25,
    "shortBreak": 5,
    "longBreak": 15,
    "pomodoroRounds": 4,
    "timezone": "Asia/Ho_Chi_Minh"
  }
  ```

### Update User Settings

- **URL**: `/api/settings/{userId}`
- **Method**: `PUT`
- **Authentication**: JWT Token
- **Request Body**: Setting object với các trường cần cập nhật
- **Response**: Updated Setting object

## Cách xác thực API

Hầu hết các API endpoints đều yêu cầu xác thực bằng JWT token. Để gọi các API này, bạn cần:

1. Đăng nhập hoặc đăng ký để lấy JWT token
2. Thêm token vào header của mỗi request:
   ```
   Authorization: Bearer your_jwt_token
   ```

## Lỗi và Status Codes

- **200 OK**: Request thành công
- **201 Created**: Tạo mới tài nguyên thành công
- **400 Bad Request**: Dữ liệu request không hợp lệ
- **401 Unauthorized**: Thiếu token hoặc token không hợp lệ
- **403 Forbidden**: Không có quyền truy cập tài nguyên
- **404 Not Found**: Không tìm thấy tài nguyên
- **500 Internal Server Error**: Lỗi server

## Cách cài đặt và chạy project

1. Sao chép file `.env.example` thành `.env` và cập nhật thông tin cấu hình
2. Cài đặt các dependencies:
   ```bash
   mvn install
   ```
3. Chạy ứng dụng:
   ```bash
   mvn spring-boot:run
   ```

## Environment Variables

Xem file `.env.example` để biết các biến môi trường cần thiết:

```
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/focusflow
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
SPRING_DATASOURCE_DRIVER_CLASSNAME=org.postgresql.Driver

# Security Configuration
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=admin

# JWT Configuration
APP_JWT_SECRET=your_jwt_secret_key_here
APP_JWT_EXPIRATION_MS=86400000
APP_JWT_REFRESH_TOKEN_EXPIRATION_MS=604800000

# Email Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password