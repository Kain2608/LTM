-- ============================================
-- KHỞI TẠO DATABASE
-- ============================================
DROP DATABASE IF EXISTS poker_online;
CREATE DATABASE poker_online CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE poker_online;

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    chips BIGINT DEFAULT 10000, 
    elo_rating INT DEFAULT 1000,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
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
-- PHÂN HỆ 2: ROOM & LOBBY (Cho Minh)
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
    status ENUM('WAITING', 'PLAYING', 'CLOSED') DEFAULT 'WAITING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(owner_id) REFERENCES users(user_id)
);

-- ============================================
-- PHÂN HỆ 3: GAME CORE / MATCH HISTORY (Cho Hoàng & An)
-- ============================================
CREATE TABLE games (
    game_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    pot_amount BIGINT DEFAULT 0,
    status ENUM('STARTING', 'PLAYING', 'FINISHED', 'CANCELLED') DEFAULT 'STARTING',
    actions_log JSON, -- Lưu toàn bộ lịch sử Check/Call/Raise/Fold dưới dạng JSON
    started_at DATETIME,
    ended_at DATETIME,
    FOREIGN KEY(room_id) REFERENCES rooms(room_id)
);

CREATE TABLE game_players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    starting_chips BIGINT,
    ending_chips BIGINT,
    chips_change BIGINT,
    result ENUM('WIN', 'LOSE', 'DRAW', 'FOLD'),
    FOREIGN KEY(game_id) REFERENCES games(game_id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users(user_id)
);

-- ============================================
-- PHÂN HỆ 4: ANALYTICS & ADMIN (Cho Thái)
-- ============================================
CREATE TABLE player_statistics (
    user_id BIGINT PRIMARY KEY,
    games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    games_lost INT DEFAULT 0,
    total_chip_won BIGINT DEFAULT 0,
    total_chip_lost BIGINT DEFAULT 0,
    biggest_win BIGINT DEFAULT 0,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE daily_statistics (
    stat_date DATE PRIMARY KEY,
    total_new_users INT DEFAULT 0,
    total_games_played INT DEFAULT 0,
    total_chips_circulated BIGINT DEFAULT 0
);


CREATE INDEX idx_user_elo ON users(elo_rating);
CREATE INDEX idx_game_room ON games(room_id);