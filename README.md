# Poker Online

Ứng dụng Texas Hold'em gồm JavaFX client và Java backend được tích hợp từ project `D:\LTM`. Backend hiện cung cấp xác thực tài khoản, hồ sơ, bạn bè, thống kê, MySQL và kết nối WebSocket có kiểm tra JWT.

## Cấu trúc

```text
.
├── database/init.sql                 # Schema và dữ liệu khởi tạo MySQL
├── docs/requirements.md              # Ghi chú yêu cầu cũ
├── src/main/java/poker/
│   ├── client/net/                   # HTTP và WebSocket client
│   ├── database/                     # Kết nối và DAO
│   ├── model/                        # Model game và dữ liệu backend
│   ├── server/http/                  # REST API server/handlers
│   ├── server/websocket/             # WebSocket server
│   ├── ui/                           # JavaFX UI
│   └── util/                         # JSON và JWT
├── src/main/resources/
│   ├── assets/                       # Asset game được đóng gói cùng ứng dụng
│   └── poker-game.css                # Theme bàn Poker
├── docker-compose.yaml
├── run.ps1                           # Chạy JavaFX client
└── run_server.ps1                    # Chạy backend
```

## Yêu cầu

- Java 17.
- Docker Desktop hoặc MySQL 8 tương thích.
- Bộ asset game chính đã được đóng gói trong project. Asset OpenDecks ở `D:\OpenDecks-Public-Domain-and-CC0-Playing-Cards` chỉ còn dùng cho các ảnh chưa đóng gói như mặt trước lá bài và avatar.

Các biến môi trường hỗ trợ được liệt kê trong `.env.example`. Tối thiểu nên đặt `POKER_DB_PASSWORD` và `POKER_JWT_SECRET`; client có thể đổi địa chỉ backend qua `POKER_API_URL` và `POKER_WS_URL`.

## Khởi động

Khởi tạo MySQL bằng Docker:

```powershell
docker compose up -d mysql-db
```

Chạy backend trong cửa sổ PowerShell thứ nhất:

```powershell
.\run_server.ps1
```

Chạy JavaFX client trong cửa sổ thứ hai:

```powershell
.\run.ps1
```

Mặc định HTTP API chạy tại `http://localhost:8080`, WebSocket tại `ws://localhost:8081`, MySQL tại `localhost:3307`.

## Phạm vi backend hiện tại

- Có: đăng ký/đăng nhập JWT, hồ sơ người dùng, danh sách/yêu cầu bạn bè, bảng xếp hạng và thống kê, WebSocket đã xác thực.
- Chưa có: API quản lý phòng/ván bài và chat. Các bảng room/game đã có trong schema nhưng chưa có DAO/service/handler tương ứng.
