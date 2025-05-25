Thiết kế một RESTful API chuẩn chỉnh là bước cực kỳ quan trọng để xây dựng hệ thống backend dễ bảo trì, dễ mở rộng và dễ hiểu cho cả team. Dưới đây là quy trình chi tiết bạn nên làm theo:

## 1. Phân tích bài toán và xác định tài nguyên (resources)
Trước tiên, hãy phân tích yêu cầu hệ thống để xác định các tài nguyên chính cần quản lý. Tài nguyên thường là các đối tượng thực tế như:

- Người dùng (Users)

- Bài viết (Posts)

- Bình luận (Comments)

- Lượt thích (Likes)

- Mối quan hệ theo dõi (Follows)

- Tương tác tin nhắn (Chats)

Mỗi tài nguyên sẽ có một endpoint riêng biệt.

## 2. Xác định các hành động (actions) trên từng tài nguyên
Với mỗi tài nguyên, bạn cần xác định các hành động cơ bản tương ứng với phương thức HTTP:

- POST: Tạo mới

- GET: Lấy dữ liệu

- PUT: Cập nhật

- DELETE: Xoá

Ví dụ:

- POST /api/posts → đăng bài viết mới

- GET /api/posts/123 → lấy chi tiết bài viết

- PUT /api/posts/123 → cập nhật bài viết

- DELETE /api/posts/123 → xoá bài viết

## 3. Thiết kế URL endpoint rõ ràng, dễ hiểu
- Dùng danh từ số nhiều, ví dụ: /users, /posts

- Không dùng động từ trong URL

- Các hành động đặc biệt dùng URL phụ: /posts/123/like

Cách đặt tên endpoint cần phản ánh rõ ràng tài nguyên đang truy cập và phương thức HTTP sẽ diễn giải hành động.

## 4. Xác định dữ liệu cần truyền (Request body và Query parameters)

- Request Body: Dùng khi tạo mới hoặc cập nhật dữ liệu.

- Query Parameters: Dùng khi lọc, tìm kiếm, phân trang dữ liệu.

- Ví dụ khi đăng bài mới:

`
{
  "caption": "Sunset at the beach",
  "image_url": "https://example.com/photo.jpg"
}
`
- Khi lấy danh sách bài viết:

`GET /api/posts?page=2&per_page=10`
## 5. Thiết kế chuẩn cấu trúc response
- Mọi API cần thống nhất định dạng phản hồi:

`{
  "status": 200,
  "message": "Success",
  "data": { ... }
}`
- Trả về đúng HTTP Status Code:

200 OK: Thành công

201 Created: Tạo thành công

400 Bad Request: Lỗi client

404 Not Found: Không tìm thấy tài nguyên

500 Internal Server Error: Lỗi server

## 6. Đảm bảo bảo mật API
- Các API quan trọng cần yêu cầu xác thực (thông qua JWT token).

- Kiểm tra quyền hạn trước khi cho phép cập nhật hoặc xoá tài nguyên.

- Ví dụ: Chỉ người tạo bài viết mới có quyền xoá bài viết đó.

## 7. Ghi tài liệu API rõ ràng
- Tài liệu API cần ghi đầy đủ:

URL

Phương thức HTTP

Request body mẫu

Response mẫu

Các mã lỗi có thể gặp

##### Một tài liệu API tốt sẽ giúp các thành viên trong team và cả bên thứ ba dễ dàng tích hợp, sử dụng API.