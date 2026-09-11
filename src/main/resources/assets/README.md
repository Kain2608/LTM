# Bundled game assets

Các ảnh trong thư mục này là asset được tạo riêng cho giao diện Poker của project:

- `backgrounds/casino-background.png`: nền casino cho bàn game.
- `backgrounds/auth-background.png`: nền riêng cho đăng nhập và đăng ký.
- `backgrounds/lobby-background.png`: nền sảnh và các màn hình sau đăng nhập.
- `backgrounds/room-browser-background.png`: nền tối, dễ đọc cho danh sách phòng.
- `table/poker-table.png`: bàn Poker nhìn từ trên xuống, nền trong suốt.
- `logo/poker-emblem.png`: biểu tượng bích và vương miện, nền trong suốt.
- `chips/premium-chip.png`: chip emerald–gold, nền trong suốt.
- `cards/card-back.png`: mặt sau lá bài, nền trong suốt.

`AssetLoader` ưu tiên các asset đóng gói ở đây, sau đó mới tìm trong đường dẫn `POKER_ASSETS_PATH` đối với các ảnh chưa được đóng gói như mặt trước bộ bài và avatar.
