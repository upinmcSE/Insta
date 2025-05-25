## Thiết kế Schema:

### Bảng `roles`: Lưu trữ thông tin quyền hạn

| Cột            | Kiểu dữ liệu       | Ràng buộc                | Mô tả                                      |
|----------------|--------------------|--------------------------|--------------------------------------------|
|id	|INT	|PRIMARY KEY, AUTO_INCREMENT	|Khóa chính, tự động tăng|
|name	|VARCHAR(255)	|NOT NULL	|Tên vai trò (ví dụ: ADMIN, USER)|
|description	|TEXT	|NULL	|Mô tả vai trò, có thể rỗng|
|created_at	|TIMESTAMP	|NOT NULL, DEFAULT CURRENT_TIMESTAMP	|Thời gian tạo|
|updated_at	|TIMESTAMP	|NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP	|Thời gian cập nhật

```sql
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```


### Bảng `users`: Lưu trữ thông tin người dùng

| Cột            | Kiểu dữ liệu       | Ràng buộc                | Mô tả                                      |
|----------------|--------------------|--------------------------|--------------------------------------------|
| id             | INT                | PRIMARY KEY, AUTO_INCREMENT | Khóa chính, tự động tăng                
| email          | VARCHAR(100)       | NOT NULL, UNIQUE         | Email, không trùng, không rỗng           |
| password_hash  | VARCHAR(255)       | NOT NULL                 | Mật khẩu đã hash                         |
| fullname       | VARCHAR(100)       | NULL                     | Họ và tên đầy đủ, có thể rỗng            |
| bio            | TEXT               | NULL                     | Tiểu sử người dùng, có thể rỗng           |
|dob	|DATETIME	|NULL	|Ngày sinh, có thể rỗng|
|gender	|ENUM('MALE', 'FEMALE', 'OTHER')	|NULL	|Giới tính, có thể rỗng|
|enabled	|BOOLEAN	|NOT NULL, DEFAULT FALSE	|Trạng thái tài khoản (kích hoạt hay chưa)|
|activation_code	|VARCHAR(4)	|NULL	|Mã xác thực tài khoản|
|verify_expired	|DATETIME	|NULL	|Thời gian hết hạn mã xác thực|
| avatar_url     | VARCHAR(255)       | NULL                     | Đường dẫn ảnh đại diện                 |
|profile_url | VARCHAR(255)| NULL | Đường dãn ảnh bìa|
| created_at     | INT                | NOT NULL                 | Thời gian tạo tài khoản                  |
| updated_at     | INT                | NOT NULL                 | Thời gian cập nhật hồ sơ lần cuối         |

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    dob DATETIME,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    bio TEXT,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    activation_code VARCHAR(255),
    verify_expired DATETIME,
    avt_url VARCHAR(255),
    profile_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```


### Bảng `files`: Lưu trữ thông tin file

| Cột            | Kiểu dữ liệu       | Ràng buộc                | Mô tả                                      |
|----------------|--------------------|--------------------------|--------------------------------------------|
|id	|VARCHAR(255)	|PRIMARY KEY	Khóa chính, |định danh file (tên file)
|content_type	|VARCHAR(255)	|NOT NULL	|Loại MIME của file (ví dụ: image/jpeg)
|path	|VARCHAR(255)	|NOT NULL	|Đường dẫn lưu trữ file (local hoặc cloud)
|size	|BIGINT	| NOT NULL	|Kích thước file (byte)
|md5_checksum	|VARCHAR(255)	|NOT NULL	|Giá trị checksum MD5 để kiểm tra tính toàn vẹn của metadata
|post_id	|BIGINT	|NULL, FOREIGN KEY	|ID bài đăng liên quan, có thể rỗng
|user_id	|BIGINT	|NOT NULL, FOREIGN KEY	|ID người dùng liên quan

```sql
CREATE TABLE files (
    id VARCHAR(255) PRIMARY KEY,
    content_type VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL UNIQUE,
    size BIGINT NOT NULL,
    md5_checksum VARCHAR(255) NOT NULL,
    post_id BIGINT,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Bảng `posts`: Lưu trữ thông tin file

| Cột            | Kiểu dữ liệu       | Ràng buộc                | Mô tả                                      |
|----------------|--------------------|--------------------------|--------------------------------------------|
|id	|BIGINT	|PRIMARY KEY, AUTO_INCREMENT	|Khóa chính, tự động tăng
|user_id	|BIGINT	|NOT NULL, FOREIGN KEY	|ID người dùng đăng bài
|caption	|TEXT	|NOT NULL	|Nội dung chú thích bài đăng
|status	|ENUM('PUBLIC', 'PRIVATE', 'DRAFT')	|NOT NULL	|Trạng thái bài đăng|
|created_at	|TIMESTAMP	|NOT NULL, DEFAULT CURRENT_TIMESTAMP	|Thời gian tạo bài đăng
|updated_at	|TIMESTAMP	|NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP	|Thời gian cập nhật bài đăng

```sql
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    caption TEXT NOT NULL,
    status ENUM('PUBLIC', 'PRIVATE', 'DRAFT'),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```



