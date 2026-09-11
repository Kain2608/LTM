-- ============================================
-- KHỞI TẠO DATABASE
-- ============================================
CREATE DATABASE IF NOT EXISTS poker_online CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE poker_online;

-- ============================================
-- PHÂN HỆ 1: USER & SOCIAL
-- ============================================
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    chips BIGINT DEFAULT 10000, 
    elo_rating INT DEFAULT 1000,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_elo (elo_rating)
);

CREATE TABLE friends (
    friendship_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'BLOCKED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_friend_user FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_friend FOREIGN KEY(friend_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE(user_id, friend_id)
);

CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('FRIEND_REQUEST', 'GAME_INVITE', 'SYSTEM'),
    content TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================
-- PHÂN HỆ 2: LOBBY & ROOM (Tiền sảnh & Phòng)
-- ============================================
CREATE TABLE rooms (
    room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    owner_id BIGINT NOT NULL,
    room_type ENUM('PUBLIC', 'PRIVATE') DEFAULT 'PUBLIC',
    password_hash VARCHAR(255),
    max_players INT DEFAULT 6,
    small_blind BIGINT DEFAULT 50,
    big_blind BIGINT DEFAULT 100,
    min_buyin BIGINT DEFAULT 1000,
    max_buyin BIGINT DEFAULT 10000,
    status ENUM('WAITING', 'PLAYING', 'CLOSED') DEFAULT 'WAITING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(owner_id) REFERENCES users(user_id)
);

CREATE TABLE room_players (
    room_player_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    seat_number INT NOT NULL, 
    is_ready BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE(room_id, seat_number),
    UNIQUE(room_id, user_id)
);

-- ============================================
-- PHÂN HỆ 3: GAME CORE & MATCH HISTORY (Ván bài)
-- ============================================
CREATE TABLE games (
    game_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    pot_amount BIGINT DEFAULT 0,
    rake_amount BIGINT DEFAULT 0, -- Tiền phế (phí sàn) thu từ ván này
    status ENUM('STARTING', 'PLAYING', 'FINISHED', 'CANCELLED') DEFAULT 'STARTING',
    actions_log JSON, -- Lưu tóm tắt diễn biến (tuỳ chọn nếu đã có bảng game_actions)
    started_at DATETIME,
    ended_at DATETIME,
    FOREIGN KEY(room_id) REFERENCES rooms(room_id),
    INDEX idx_game_room (room_id)
);

CREATE TABLE game_players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    seat_number INT NOT NULL,
    starting_chips BIGINT,
    ending_chips BIGINT,
    chips_change BIGINT,
    total_bet BIGINT DEFAULT 0,
    result ENUM('WIN', 'LOSE', 'DRAW', 'FOLD'),
    status ENUM('WAITING', 'PLAYING', 'FOLDED', 'ALL_IN', 'SITTING_OUT') DEFAULT 'PLAYING',
    FOREIGN KEY(game_id) REFERENCES games(game_id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users(user_id)
);

CREATE TABLE game_actions (
    action_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    betting_round ENUM('PREFLOP', 'FLOP', 'TURN', 'RIVER') NOT NULL,
    action_type ENUM('SMALL_BLIND', 'BIG_BLIND', 'CHECK', 'CALL', 'RAISE', 'FOLD', 'ALL_IN') NOT NULL,
    amount BIGINT DEFAULT 0, 
    action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(game_id) REFERENCES games(game_id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users(user_id)
);

-- ============================================
-- PHÂN HỆ 4: ECONOMY & ANTI-CHEAT (Kinh tế)
-- ============================================
CREATE TABLE chip_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount BIGINT NOT NULL, 
    balance_before BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    source_type ENUM(
        'IAP_PURCHASE', 'DAILY_REWARD', 'GAME_BUY_IN', 
        'GAME_CASH_OUT', 'GAME_RAKE', 'ADMIN_BONUS', 
        'ADMIN_PENALTY', 'SYSTEM_REFUND'
    ) NOT NULL,
    reference_id BIGINT, 
    ip_address VARCHAR(45),
    device_id VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(user_id),
    INDEX idx_user_history (user_id, created_at),
    INDEX idx_source_ref (source_type, reference_id)
);

-- ============================================
-- PHÂN HỆ 5: ANALYTICS & RANKING (Thống kê & Xếp hạng)
-- ============================================
CREATE TABLE player_statistics (
    user_id BIGINT PRIMARY KEY,
    games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    games_lost INT DEFAULT 0,
    total_chip_won BIGINT DEFAULT 0,
    total_chip_lost BIGINT DEFAULT 0,
    biggest_win BIGINT DEFAULT 0,
    biggest_pot_won BIGINT DEFAULT 0,
    total_play_time_seconds BIGINT DEFAULT 0,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE daily_statistics (
    stat_date DATE PRIMARY KEY,
    total_new_users INT DEFAULT 0,
    total_active_users INT DEFAULT 0,
    total_games_played INT DEFAULT 0,
    total_rooms_created INT DEFAULT 0,
    total_chips_circulated BIGINT DEFAULT 0,
    total_wins INT DEFAULT 0
);

CREATE TABLE elo_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    game_id BIGINT,
    elo_before INT NOT NULL,
    elo_after INT NOT NULL,
    elo_change INT NOT NULL,
    reason VARCHAR(255) DEFAULT 'GAME_RESULT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY(game_id) REFERENCES games(game_id) ON DELETE SET NULL
);
