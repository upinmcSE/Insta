## I. Quy trình xác thực (Authentication Flow) trong Poops
Để đảm bảo an toàn cho hệ thống và dữ liệu người dùng, Sydegram áp dụng một quy trình xác thực rõ ràng và chặt chẽ. Bài giảng này sẽ giúp bạn hiểu từng bước trong flow xác thực từ đăng ký, đăng nhập, sử dụng token, cho đến logout.

### 1. Đăng ký tài khoản (Register)
Người dùng gửi thông tin đăng ký (username, password, fullname, email) tới API /api/auth/register bằng phương thức POST.

Server kiểm tra hợp lệ dữ liệu:

- Username chưa tồn tại

- Email hợp lệ và chưa tồn tại

- Password đủ mạnh

Nếu thành công, server gửi 1 mã xác thực đến email vừa đăng ký
Nếu có lỗi, server trả về mã lỗi 400 hoặc 500 kèm thông báo chi tiết.

Ví dụ Request:

`
{
  "username": "newuser",
  "password": "mypassword",
  "fullname": "New User",
  "email": "newuser@example.com"
}
`
Ví dụ Response:

`
{
  "message": "Hãy thực hiện xác thực",
}
`

### 2. Xác thực tài khoản (Verify)



### 3. Đăng nhập (Login)
Người dùng gửi username và password tới API /api/auth/login bằng phương thức POST.

Server kiểm tra:

- Username tồn tại không?

- Password đúng không?

Nếu đúng:

- Server tạo ra Access Token và Refresh Token.

Nếu sai:

- Server trả về lỗi 401 Unauthorized.

Ví dụ Request:

`
{
  "username": "newuser",
  "password": "mypassword"
}
`

Ví dụ Response:

`
{
  "message": "Login successful",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 10,
      "username": "newuser",
      "fullname": "New User",
      "email": "newuser@example.com"
    }
  }
}
`

### 4. Sử dụng Access Token để gọi API
Sau khi đăng nhập, mỗi lần gọi API cần đính kèm Access Token vào header:

Authorization: Bearer <access_token>

Ví dụ: Request lấy hồ sơ cá nhân:

`GET /api/user/profile`
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Nếu token hợp lệ, server trả về thông tin người dùng. Nếu token sai hoặc hết hạn, server trả về 401 Unauthorized.

### 5. Đăng xuất (Logout)
Khi người dùng chọn "Logout", frontend sẽ gọi API /api/auth/logout bằng POST, kèm Access Token.

Server xác nhận và trả về thông báo logout thành công.

Ví dụ Request:

`POST /api/auth/logout`
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Ví dụ Response:

`
{
  "message": "Logout successful"
}
`

Sơ đồ tóm tắt flow xác thực

`1. Đăng ký tài khoản ➔ 2.Xác thực tài khoản ➔ 3. Đăng nhập ➔ 4. Nhận Access/Refresh Token ➔ 5. Gọi các API kèm Access Token ➔ 6. Logout, ignore token phía server xoá token phía client.`

Một số lưu ý bảo mật quan trọng:
- Không trả về hoặc lưu password dưới dạng plain text.

- Token nên có thời gian hết hạn hợp lý (ví dụ 15 phút cho Access Token).

- Sử dụng HTTPS để bảo vệ token khỏi bị nghe lén.

- Xác thực tất cả các API quan trọng, không dựa vào client-side logic.

## II. Quy trình lấy lại mật khẩu (Forgot password Flow)


## III. User Endpoints

Tất cả các API quản lý người dùng đều yêu cầu Access Token hợp lệ, được gửi thông qua header Authorization: Bearer <access_token>.

Các chức năng chính:

- Lấy thông tin hồ sơ cá nhân

- Xem hồ sơ người dùng khác

- Cập nhật hồ sơ cá nhân

- Theo dõi và bỏ theo dõi người dùng

- Lấy danh sách bài viết của người dùng với phân trang

### 1. Lấy hồ sơ cá nhân (Get Current User Profile)
URL: /api/users/profile

Method: GET

Headers:

Authorization: Bearer <access_token>

Ví dụ Request
GET /api/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Ví dụ Response
{
  "data": {
    "id": 1,
    "username": "newuser",
    "fullname": "New User",
    "email": "newuser@example.com",
    "bio": "Hello world!",
    "follower_count": 100,
    "following_count": 150
  }
}

### 2. Xem hồ sơ người dùng khác (View Another User's Profile)
URL: /api/users/<user_id>/profile

Method: GET

Headers:

Authorization: Bearer <access_token>

Ví dụ Request
GET /api/users/5/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Ví dụ Response
{
  "data": {
    "id": 5,
    "username": "frienduser",
    "fullname": "Friend User",
    "bio": "Love photography",
    "follower_count": 250,
    "following_count": 180
  }
}
Lỗi có thể gặp
404 Not Found: Người dùng không tồn tại.

### 3. Chỉnh sửa hồ sơ cá nhân (Edit Own Profile)
URL: /api/users/profile

Method: PUT

Headers:

Authorization: Bearer <access_token>

Request Body mẫu
{
  "fullname": "Updated User",
  "email": "updateduser@example.com",
  "username": "updateduser",
  "bio": "Loving life and tech."
}
Ví dụ Response thành công
{
  "message": "Update profile successfully",
  "data": {
    "id": 1,
    "username": "updateduser",
    "fullname": "Updated User",
    "email": "updateduser@example.com",
    "bio": "Loving life and tech."
  }
}
Các lỗi có thể gặp
400 Bad Request: Không có trường hợp lệ cần cập nhật hoặc username/email đã tồn tại.

500 Internal Server Error: Lỗi server khi cập nhật hồ sơ.

### 4. Lấy danh sách bài viết của người dùng (Get User's Posts - Paginated)
URL: /api/users/<user_id>/posts

Method: GET

Headers:

Authorization: Bearer <access_token>

Query Parameters
page: số trang (mặc định 1)

per_page: số lượng bài viết mỗi trang (mặc định 10)

Ví dụ Request
GET /api/users/5/posts?page=1&per_page=5
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Ví dụ Response
{
  "items": [
    {
      "id": 101,
      "image_url": "https://example.com/photo1.jpg",
      "caption": "Sunset by the sea",
      "created_at": 1714000000
    },
    {
      "id": 102,
      "image_url": "https://example.com/photo2.jpg",
      "caption": "Morning coffee",
      "created_at": 1714003600
    }
  ],
  "pagination": {
    "page": 1,
    "per_page": 5,
    "total": 25
  }
}
### 5. Theo dõi người dùng khác (Follow User)
URL: /api/users/<user_id>/follow

Method: POST

Headers:

Authorization: Bearer <access_token>

Ví dụ Request
POST /api/users/5/follow
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Success Response
{
  "message": "Followed user successfully",
  "data": null
}
Error Responses
400 Bad Request: Không thể tự theo dõi chính mình hoặc đã theo dõi người này.

404 Not Found: Người dùng không tồn tại.

### 6. Bỏ theo dõi người dùng khác (Unfollow User)
URL: /api/users/<user_id>/follow

Method: DELETE

Headers:

Authorization: Bearer <access_token>

Ví dụ Request
DELETE /api/users/5/follow
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Success Response
{
  "message": "Unfollowed user successfully",
  "data": null
}
Error Responses
400 Bad Request: Không thể bỏ theo dõi chính mình hoặc chưa từng theo dõi người này.

404 Not Found: Người dùng không tồn tại.

500 Internal Server Error: Lỗi khi bỏ theo dõi.

Lưu ý khi làm việc với User API
Các API cần token xác thực hợp lệ.

Khi cập nhật hồ sơ, backend cần kiểm tra xem email hoặc username có bị trùng với người khác không.

Cần xử lý phân trang chuẩn để đảm bảo hiệu suất cao khi lấy danh sách bài viết.

Khi thao tác follow/unfollow cần kiểm tra các điều kiện hợp lý để tránh lỗi logic.


## III. Post Endpoints

### 1. Tạo bài viết mới (Create Post)
URL: /api/posts

Method: POST

Headers:

Authorization: Bearer <access_token>

Request Body
{
  "caption": "Sunset at the beach",
  "image_url": "https://example.com/photo.jpg"
}
Ví dụ Request
POST /api/posts
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "caption": "Sunset at the beach",
  "image_url": "https://example.com/photo.jpg"
}
Success Response
{
  "message": "Create post successfully",
  "data": {
    "id": 101,
    "caption": "Sunset at the beach",
    "image_url": "https://example.com/photo.jpg",
    "created_at": 1714000000
  }
}
Error Responses
400 Bad Request: Thiếu hình ảnh.

500 Internal Server Error: Lỗi server khi tạo bài viết.

### 2. Xoá bài viết (Delete Post)
URL: /api/posts/<post_id>

Method: DELETE

Headers:

Authorization: Bearer <access_token>

Ví dụ Request
DELETE /api/posts/101
Authorization: Bearer <access_token>
Success Response
{
  "message": "Post deleted successfully"
}
Error Responses
404 Not Found: Bài viết không tồn tại.

403 Forbidden: Không có quyền xoá bài viết này.

500 Internal Server Error: Lỗi server khi xoá bài viết.

### 3. Thích bài viết (Like Post)
URL: /api/posts/<post_id>/like

Method: POST

Headers:

Authorization: Bearer <access_token>

Ví dụ Request
POST /api/posts/101/like
Authorization: Bearer <access_token>
Success Response
{
  "message": "Liked post successfully"
}
Error Responses
404 Not Found: Bài viết không tồn tại.

400 Bad Request: Đã thích bài viết này rồi.

### 4. Bỏ thích bài viết (Unlike Post)
URL: /api/posts/<post_id>/like

Method: DELETE

Headers:

Authorization: Bearer <access_token>

Ví dụ Request
DELETE /api/posts/101/like
Authorization: Bearer <access_token>
Success Response
{
  "message": "Unliked post successfully"
}
Error Responses
404 Not Found: Bài viết không tồn tại.

400 Bad Request: Bạn chưa từng thích bài viết này.

500 Internal Server Error: Lỗi server khi bỏ thích.

### 5. Xem News Feed (View News Feed)
URL: /api/posts/newsfeed

Method: GET

Headers:

Authorization: Bearer <access_token>

Query Parameters
page: số trang (mặc định 1)

per_page: số lượng bài viết mỗi trang (mặc định 10)

Ví dụ Request
GET /api/posts/newsfeed?page=1&per_page=5
Authorization: Bearer <access_token>
Success Response
{
  "data": {
    "items": [
      {
        "id": 101,
        "caption": "Sunset at the beach",
        "image_url": "https://example.com/photo.jpg",
        "user": {
          "id": 5,
          "username": "frienduser"
        },
        "like_count": 120,
        "liked_by_current_user": true,
        "created_at": 1714000000
      }
    ],
    "pagination": {
      "page": 1,
      "per_page": 5,
      "total": 25,
      "pages": 5
    }
  }
}
Lưu ý khi làm việc với Post API
Tất cả các API liên quan đến bài viết yêu cầu Access Token hợp lệ.

Khi tạo bài viết, cần kiểm tra bắt buộc có hình ảnh.

Chỉ chủ nhân bài viết mới được quyền xoá bài viết của mình.

Các thao tác Like và Unlike phải kiểm tra trạng thái hiện tại để tránh lỗi.

Phân trang News Feed để đảm bảo hiệu suất hệ thống và trải nghiệm người dùng.