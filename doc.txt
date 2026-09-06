Game Poker Online Multiplayer
Hệ thống gồm một Server và nhiều Client. Server lưu trữ toàn bộ thông tin người dùng, phòng chơi, trạng thái các ván Poker, chip, lịch sử trận đấu, bảng xếp hạng và các thông tin thống kê của hệ thống.
Để tham gia hệ thống, người chơi phải đăng ký tài khoản và đăng nhập từ một máy Client. Sau khi đăng nhập thành công, người chơi được chuyển tới giao diện Lobby.
Tại Lobby, hệ thống hiển thị danh sách các phòng Poker đang hoạt động. Mỗi phòng có các thông tin như: mã phòng, tên phòng, chủ phòng, số người hiện tại, số người tối đa, Small Blind, Big Blind, lượng chip Buy-in, loại phòng và trạng thái phòng.
Danh sách phòng được cập nhật theo thời gian thực. Khi có phòng mới được tạo, người chơi tham gia hoặc rời phòng, hoặc trạng thái phòng thay đổi thì Server sẽ gửi thông báo tới các Client đang ở Lobby.
Người chơi có thể tạo phòng Poker mới hoặc tham gia một phòng đã tồn tại. Khi tạo phòng, người chơi có thể cấu hình tên phòng, số người tối đa, Small Blind, Big Blind, Buy-in và loại phòng Public hoặc Private.
Đối với phòng Private, người chơi muốn tham gia phải nhập đúng mật khẩu của phòng.
Sau khi tham gia phòng, người chơi có thể chọn trạng thái Ready hoặc Unready. Khi đủ số lượng người chơi theo quy định và các người chơi đều sẵn sàng, Server bắt đầu một ván Poker.
Hệ thống sử dụng luật Texas Hold’em. Khi bắt đầu một ván, Server xác định Dealer, Small Blind, Big Blind, xáo bộ bài và chia cho mỗi người chơi 2 lá bài riêng.
Các lá bài riêng của mỗi người chơi chỉ được Server gửi tới đúng Client của người chơi đó. Người chơi khác và người xem không được biết các lá bài riêng này.
Một ván Poker gồm các giai đoạn: Pre-Flop, Flop, Turn, River và Showdown.
Khi đến lượt, người chơi có thể thực hiện một trong các hành động hợp lệ như: Fold, Check, Call, Bet, Raise hoặc All-in.
Mỗi hành động của người chơi được gửi lên Server. Server sẽ kiểm tra người chơi có đúng lượt hay không, hành động có hợp lệ hay không, số chip có đủ hay không và sau đó mới cập nhật trạng thái chính thức của ván chơi.
Server chịu trách nhiệm quản lý toàn bộ chip, tiền cược, Main Pot và Side Pot. Client không được phép tự thay đổi số chip hoặc kết quả của ván chơi.
Mỗi lượt chơi có một khoảng thời gian giới hạn. Server quản lý Timer và đồng bộ thời gian tới các Client. Nếu người chơi hết thời gian, Server sẽ tự động Check nếu có thể hoặc Fold nếu không thể Check.
Sau các vòng cược, nếu còn từ hai người chơi trở lên thì Server thực hiện Showdown, đánh giá bộ bài của từng người và xác định người thắng.
Nếu tất cả người chơi trừ một người đã Fold thì Server kết thúc ván ngay và người chơi còn lại nhận Pot.
Sau khi kết thúc một ván, Server phân phối Pot cho người thắng, cập nhật số chip, lưu lịch sử ván đấu và cập nhật thống kê của người chơi.
Trong phòng chơi, các người chơi có thể chat với nhau theo thời gian thực thông qua WebSocket.
Hệ thống hỗ trợ Spectator, cho phép người dùng theo dõi ván Poker nhưng không được thực hiện các hành động chơi và không được xem bài riêng của người chơi.
Nếu một người chơi bị mất kết nối trong khi đang chơi, Server vẫn giữ trạng thái của người chơi trong một khoảng thời gian. Khi người chơi kết nối lại, Server xác thực tài khoản và gửi lại trạng thái hiện tại của ván để người chơi tiếp tục.
Người chơi có thể xem lịch sử các trận đấu đã tham gia và các thông tin thống kê cá nhân như tổng số trận, số trận thắng, số trận thua, tỷ lệ thắng, tổng chip và thời gian chơi.
Mỗi người chơi có thể xem bảng xếp hạng toàn hệ thống theo các tiêu chí như Ranking Point, tổng số trận thắng, tổng số chip hoặc tỷ lệ thắng.
Hệ thống có tài khoản Admin để quản lý người dùng, khóa/mở khóa tài khoản, theo dõi các phòng đang hoạt động, theo dõi các game đang diễn ra và xem các số liệu thống kê của hệ thống.
