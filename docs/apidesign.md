# Tài liệu API



### UC04: Xem hồ sơ cá nhân (View Own Profile)

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

  "website": "https://sydexa.com",

  "profile_picture_url": "https://sydexa.com/dat.jpg",

  "posts_count": 120,

  "followers_count": 3000,

  "following_count": 250,

  "is_private": false,

  "is_verified": true,

  "joined_at": "2024-01-01T08:00:00Z",

  "email": "dat@example.com",

  "phone_number": "+8412345678",

  "allow_push_notifications": true,

  "two_factor_enabled": false

}
```


### UC05: Chỉnh sửa hồ sơ cá nhân (Edit Own Profile)

##### Phân tích yêu cầu
Trước khi bắt đầu triển khai, hãy xác định các yêu cầu cho chức năng này:

- Người dùng phải đăng nhập để chỉnh sửa hồ sơ
- Người dùng chỉ có thể chỉnh sửa hồ sơ của chính mình
- Các trường có thể chỉnh sửa: tên đầy đủ, email, tên người dùng, tiểu sử
- Tên người dùng và email phải là duy nhất trong hệ thống
- Cần xác thực dữ liệu đầu vào








### UC06: Xem hồ sơ người dùng khác (View Other User's Profile)

#### Phân tích yêu cầu
Trước khi bắt đầu triển khai, hãy xác định các yêu cầu cho chức năng này:

- Người dùng phải đăng nhập để xem hồ sơ người dùng khác
- Người dùng có thể xem hồ sơ của bất kỳ người dùng nào trong hệ thống
- Thông tin hiển thị bao gồm: tên người dùng, tên đầy đủ, tiểu sử, v.v.
- Cần xử lý trường hợp người dùng không tồn tại