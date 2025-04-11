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
- [Streak](#streak)
- [Settings](#settings)
- [Notifications](#notifications)
- [Payment](#payment)

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

### Update Last Active Time

- **URL**: `/api/user-profile/{userId}/last-active`
- **Method**: `PUT`
- **Authentication**: JWT Token
- **Request Param**: `fcmToken`
- **Response**: HTTP Status 200 (OK)

### Handle App Shutdown

- **URL**: `/api/user-profile/{userId}/shutdown`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Param**: `fcmToken`
- **Response**: HTTP Status 200 (OK)

## Payment

### Create MoMo Payment

- **URL**: `/api/payment/momo/create`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "amount": 10000,
    "orderInfo": "Thanh toán đơn hàng FocusFlow"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "paymentUrl": "https://payment.momo.vn/...",
    "orderId": "ORDER-uuid",
    "message": "Success"
  }
  ```

### MoMo Return URL

- **URL**: `/api/payment/momo/return`
- **Method**: `GET`
- **Authentication**: Không cần
- **Request Params**: Query parameters từ MoMo
- **Response**: Text message

### MoMo Notify URL

- **URL**: `/api/payment/momo/notify`
- **Method**: `POST`
- **Authentication**: Không cần
- **Request Body**: Notification data từ MoMo
- **Response**: "OK" hoặc error message

### Create VNPay Payment

- **URL**: `/api/payment/vnpay/create`
- **Method**: `POST`
- **Authentication**: JWT Token
- **Request Body**:
  ```json
  {
    "amount": 10000,
    "orderInfo": "Thanh toán đơn hàng FocusFlow"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "paymentUrl": "https://sandbox.vnpayment.vn/...",
    "orderId": "ORDER-uuid",
    "message": "Success"
  }
  ```

### VNPay Return URL

- **URL**: `/api/payment/vnpay/return`
- **Method**: `GET`
- **Authentication**: Không cần
- **Request Params**: Query parameters từ VNPay
- **Response**: Text message

## Projects, Tasks, Subtasks, Tags, Pomodoro, Streak, Settings, Notifications

*Chi tiết về các API endpoints khác sẽ được bổ sung trong các phiên bản README tiếp theo.*

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

Xem file `.env.example` để biết các biến môi trường cần thiết.