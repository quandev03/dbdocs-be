# Cấu hình CORS cho DBDocs

Hướng dẫn này giúp bạn cấu hình CORS (Cross-Origin Resource Sharing) cho ứng dụng DBDocs, cho phép các domain frontend khác nhau gọi API của backend.

## Cấu hình bằng biến môi trường

Cách đơn giản nhất để cấu hình CORS là sử dụng biến môi trường `CORS_ALLOWED_ORIGINS`. Bạn có thể đặt biến này trong file `.env` ở thư mục gốc của dự án:

```
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:3000
```

Trong ví dụ trên, chúng ta cho phép các domain `http://localhost:4200` (Angular) và `http://localhost:3000` (React) gọi API của backend.

## Tạo file .env

1. Tạo file `.env` ở thư mục gốc của dự án
2. Sao chép nội dung từ file `src/main/resources/env-example.txt`
3. Cập nhật các giá trị phù hợp với môi trường của bạn

## Cấu hình trong application.yml

Nếu bạn không muốn sử dụng biến môi trường, bạn có thể cấu hình trực tiếp trong file `application.yml`:

```yaml
# CORS Configuration
cors:
  allowed-origins: http://localhost:4200,http://localhost:3000
```

## Cấu hình cho môi trường phát triển

Trong môi trường phát triển, bạn có thể cho phép tất cả các origin bằng cách đặt:

```
CORS_ALLOWED_ORIGINS=*
```

**Lưu ý:** Không sử dụng cấu hình này trong môi trường production vì lý do bảo mật.

## Kiểm tra cấu hình CORS

Để kiểm tra xem cấu hình CORS có hoạt động không, bạn có thể thực hiện các bước sau:

1. Chạy backend DBDocs
2. Mở console trong trình duyệt và thực hiện request AJAX đến backend:

```javascript
fetch('http://localhost:8080/api/v1/projects', {
  headers: {
    'Authorization': 'Bearer your-token-here'
  }
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

Nếu không có lỗi CORS, request sẽ thành công.

## Xử lý lỗi CORS

Nếu bạn gặp lỗi CORS, hãy kiểm tra:

1. Domain của frontend có trong danh sách `CORS_ALLOWED_ORIGINS` không
2. Backend có đang chạy và lắng nghe trên port đúng không
3. Request có gửi đúng header không (ví dụ: Authorization)

## Cấu hình nâng cao

Nếu bạn cần cấu hình CORS nâng cao, bạn có thể chỉnh sửa file `CorsConfig.java` trong package `com.vissoft.vn.dbdocs.infrastructure.config`. 