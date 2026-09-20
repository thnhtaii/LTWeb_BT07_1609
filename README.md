# BT07_1609 - Spring Boot 3 RESTful API, Swagger 3 & AJAX CRUD

Dự án bài tập thực hành môn Lập trình Web xây dựng ứng dụng Web hoàn chỉnh trên nền tảng **Spring Boot 3**, kết nối cơ sở dữ liệu **Microsoft SQL Server**, cung cấp hệ thống **RESTful API** có tài liệu **Swagger 3 (OpenAPI)** và giao diện **AJAX (Bootstrap 5, jQuery)** không tải lại trang.

Dự án tích hợp đầy đủ yêu cầu từ 3 tài liệu hướng dẫn:
1. **CRUD API Category trên Spring Boot 3** (Xử lý thực thể Category, Product, Multipart File Upload, Service, Repository, Controller API).
2. **Cấu hình Swagger 3 trên Spring Boot 3** (Springdoc OpenAPI, nhóm API, cổng 8082).
3. **AJAX với RESTful API trong Spring Boot** (Hiển thị danh sách, thêm, sửa, xóa Category bằng jQuery AJAX và phần bài tập thêm quản lý Product bằng AJAX).

---

## 1. Công nghệ sử dụng

- **Backend**:
  - Spring Boot 3.3.4
  - Java 21 (tương thích chạy trên JDK 21+)
  - Spring Data JPA, Hibernate ORM
  - Microsoft SQL Server (`mssql-jdbc`)
  - Springdoc OpenAPI Starter WebMVC UI 2.6.0 (Swagger 3)
  - Apache Commons IO (hỗ trợ xử lý mở rộng file và upload)
- **Frontend**:
  - HTML5, CSS3, JavaScript
  - jQuery 3.6.4
  - Bootstrap 5.0.2
  - Font Awesome 6, Boxicons
- **Database**:
  - Microsoft SQL Server (Database: `BT07`)

---

## 2. Cấu trúc thư mục nguồn

```
src/main/java/vn/iotstar/
├── Bt071609Application.java             # Main Application & Storage Runner
├── config/
│   ├── StorageProperties.java           # Cấu hình đường dẫn uploads
│   └── SwaggerConfig.java               # Cấu hình Springdoc OpenAPI / Swagger 3
├── controller/
│   ├── ProductController.java           # Phục vụ xem ảnh /admin/{categories,products}/images/
│   └── api/
│       ├── CategoryAPIController.java   # REST API CRUD Category & Upload Icon
│       └── ProductAPIController.java    # REST API CRUD Product (Bài tập thêm)
├── entity/
│   ├── Category.java                    # Entity Category (One-To-Many Product)
│   └── Product.java                     # Entity Product (Many-To-One Category)
├── exception/
│   ├── StorageException.java            # Xử lý lỗi lưu trữ file
│   └── StorageFileNotFoundException.java# Xử lý lỗi không tìm thấy file
├── model/
│   ├── ProductModel.java                # DTO truyền dữ liệu Product
│   └── Response.java                    # Định dạng chuẩn phản hồi API {status, message, body}
├── repository/
│   ├── CategoryRepository.java          # JpaRepository cho Category
│   └── ProductRepository.java           # JpaRepository cho Product
└── service/
    ├── ICategoryService.java            # Interface nghiệp vụ Category
    ├── IProductService.java             # Interface nghiệp vụ Product
    ├── IStorageService.java             # Interface xử lý File Upload
    └── impl/
        ├── CategoryServiceImpl.java     # Cài đặt dịch vụ Category
        ├── FileSystemStorageServiceImpl.java # Cài đặt lưu file uploads/
        └── ProductServiceImpl.java      # Cài đặt dịch vụ Product

src/main/resources/
├── application.properties               # Cấu hình database, cổng 8082, upload, swagger
└── static/
    ├── index.html                       # Tự động chuyển hướng về /ajax.html
    └── ajax.html                        # Giao diện chính AJAX CRUD Category & Product
```

---

## 3. Hướng dẫn cấu hình & Cài đặt

### 3.1. Cấu hình cơ sở dữ liệu (`application.properties`)
Đảm bảo SQL Server đang chạy và đã tạo cơ sở dữ liệu `BT07`:
```properties
server.port=8082

spring.datasource.url=jdbc:sqlserver://localhost:64590;databaseName=BT07;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=1234567@a$
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

storage.location=uploads

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

---

## 4. Hướng dẫn chạy dự án

### Cách 1: Chạy trực tiếp trong Eclipse / Spring Tools Suite (STS)
1. Mở dự án `BT07_1609` trong Eclipse STS.
2. Chuột phải vào project chọn **Run As** -> **Spring Boot App**.
3. Quan sát console khi hiển thị `Tomcat started on port 8082`.

### Cách 2: Chạy bằng dòng lệnh Maven / Terminal
Đóng gói dự án:
```powershell
mvn clean package -DskipTests
```

Khởi chạy ứng dụng:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2.1"
& "$env:JAVA_HOME\bin\java.exe" -jar target\BT07_1609-0.0.1-SNAPSHOT.jar
```

---

## 5. Danh sách các đường dẫn & API Endpoints

### 5.1. Giao diện người dùng
| Chức năng | Đường dẫn URL | Mô tả |
| :--- | :--- | :--- |
| **Giao diện AJAX CRUD** | `http://localhost:8082/ajax.html` | Trang quản lý danh mục và sản phẩm bằng AJAX |
| **Swagger UI (OpenAPI 3)** | `http://localhost:8082/swagger-ui/index.html` | Giao diện tra cứu và kiểm thử trực tiếp các API |
| **OpenAPI Specification** | `http://localhost:8082/v3/api-docs` | Dữ liệu mô tả chuẩn OpenAPI JSON |

### 5.2. Category RESTful API (`/api/category`)
| Phương thức | Endpoint | Mô tả | Định dạng dữ liệu |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/category` | Lấy toàn bộ danh mục | JSON Response |
| **POST** | `/api/category/getCategory?id={id}` | Lấy chi tiết một danh mục | Query Param |
| **POST** | `/api/category/addCategory` | Thêm danh mục mới | Multipart form-data (`categoryName`, `icon`) |
| **PUT** | `/api/category/updateCategory` | Cập nhật danh mục | Multipart form-data (`categoryId`, `categoryName`, `icon`) |
| **DELETE** | `/api/category/deleteCategory?categoryId={id}` | Xóa danh mục và file icon | Query Param |
| **GET** | `/api/category/images/{filename}` | Xem ảnh icon đã upload | Image Resource Stream |

### 5.3. Product RESTful API (`/api/product`) - Bài tập mở rộng
| Phương thức | Endpoint | Mô tả | Định dạng dữ liệu |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/product` | Lấy toàn bộ sản phẩm | JSON Response |
| **POST** | `/api/product/getProduct?id={id}` | Lấy chi tiết sản phẩm | Query Param |
| **POST** | `/api/product/addProduct` | Thêm sản phẩm kèm ảnh và danh mục | Multipart form-data |
| **PUT** | `/api/product/updateProduct` | Cập nhật thông tin sản phẩm | Multipart form-data |
| **DELETE** | `/api/product/deleteProduct?productId={id}` | Xóa sản phẩm theo mã | Query Param |
| **GET** | `/admin/products/images/{filename}` | Xem ảnh sản phẩm trực tiếp | Image Resource Stream |

---

## 6. Tính năng nổi bật của giao diện AJAX

1. **Category AJAX CRUD**:
   - Tải dữ liệu bất đồng bộ không cần reload trang bằng jQuery `$.getJSON`.
   - Hiển thị ảnh icon tương ứng của từng Category.
   - Thêm Category qua Modal Bootstrap 5, upload icon tức thì.
   - Cập nhật Category: tự động đổ dữ liệu cũ vào Modal Form và gửi `PUT` bất đồng bộ.
   - Xóa Category: hộp thoại xác nhận `confirm`, gọi `DELETE` và làm mờ biến mất với hiệu ứng `fadeOut('slow')`.
2. **Product AJAX CRUD (Bài tập thêm)**:
   - Danh sách sản phẩm đầy đủ giá, số lượng, danh mục liên kết và hình ảnh đại diện.
   - Thêm sản phẩm mới và chọn danh mục động từ cơ sở dữ liệu qua combobox.
   - Xóa sản phẩm tức thì bằng AJAX.
