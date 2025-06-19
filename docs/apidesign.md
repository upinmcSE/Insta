# Tài liệu API


### UC01: Đăng ký tài khoản

### UC02: Xác thực email

### UC03: Đăng nhập



### UC04: Lấy lại phiên đăng nhập (refresh token)


### UC05: Quên mật khẩu



### UC06: ResetPassword

##### Thông tin kỹ thuật
- Mục đích: Đổi mật khẩu khi người dùng đã xác thực thành công


### UC07: Đăng xuất



### UC08: Xem hồ sơ cá nhân 

##### Thông tin kỹ thuật

- Mục đích: Lấy thông tin của người dùng đang đăng nhập.


| Thành hần       | Đặc tả |
|-----------------|-----------------|
| Method              | GET   |
| Endpoint            | /api/v1/me   |
| Authentication| Bắt buộc. Server cần biết ai đang yêu cầu để trả về đúng hồ sơ|
| Request |Không cần thêm tham số nào ngoài thông tin xác thực|
| Response (Success) |	JSON chứa thông tin người dùng (ví dụ: user_id, username, email, full_name, bio, profile_picture_url, created_at) |
| Response (Error) |401 Unauthorized (nếu chưa đăng nhập), 500 Internal Server Error (lỗi server) |

##### Response (Success)

Nếu hợp lệ, API trả về thông tin người dùng dưới dạng JSON:

```{

  "user_id": "123456789",

  "username": "datbavip10",

  "full_name": "Đỗ Thành Đạt",

  "bio": "Code by day, dream by night",

  "profile_picture_url": "https://poops.com/dat.jpg",

  "posts_count": 120,

  "followers_count": 3000,

  "following_count": 250,

  "is_verified": true,

  "joined_at": "2024-01-01T08:00:00Z",

  "email": "dat@example.com",

}
```



### UC09: Chỉnh sửa hồ sơ cá nhân (Edit Own Profile)

##### Phân tích yêu cầu

- Người dùng phải đăng nhập để chỉnh sửa hồ sơ
- Người dùng chỉ có thể chỉnh sửa hồ sơ của chính mình
- Các trường có thể chỉnh sửa: tên người dùng, tiểu sử, ngày sinh, giới tính
- Tên người dùng và email phải là duy nhất trong hệ thống
- Cần xác thực dữ liệu đầu vào




### UC10: Xem hồ sơ người dùng khác 

#### Phân tích yêu cầu

- Người dùng phải đăng nhập để xem hồ sơ người dùng khác
- Người dùng có thể xem hồ sơ của bất kỳ người dùng nào trong hệ thống
- Thông tin hiển thị bao gồm: tên người dùng, tiểu sử, v.v.

### UC11: Thay đổi avt-image



### UC12: Thay đổi profile-image


### UC13: Thay đổi mật khẩu


### UC14: Theo dõi người dùng


### UC15: Bỏ theo dõi người dùng


### UC16: Chặn người dùng

### UC17: Tìm kiếm user

### UC18: Tạo bài viết

### UC19: Xem chi tiết bài viết

### UC20: Xem tất cả bài viết của người dùng

### UC21: Xóa bài viết


### UC22: Sửa bài viết

### UC23: Ẩn bài viết

### UC24: New feed (global | network)

### UC25: Bình luận bài viết

### UC26: Chỉnh sửa bình luận

### UC27: Xóa bình luận

### UC28: Trả lời bình luận

### UC29: Chỉnh sửa trả lời bình luận

### UC30: Xóa trả lời bình luận

### UC31: Gửi tin nhắn

### UC32: Đọc tin nhắn

### UC33: Xóa tin nhắn

### UC34: Xem lịch sử tin nhắn

### UC35: Push thông báo (new_post | comment_post | like_post)

### UC36: Đọc tin nhắn
