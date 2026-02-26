-- ============================================================
-- RevConnect Database Schema
-- NOTE: Hibernate auto-creates all tables via ddl-auto=update
-- This file is for reference, ERD creation, and manual setup only
-- ============================================================

CREATE DATABASE IF NOT EXISTS revconnect
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE revconnect;

-- ─── Users ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  UNIQUE NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    password        VARCHAR(255) NOT NULL COMMENT 'BCrypt hashed',
    role            ENUM('PERSONAL','CREATOR','BUSINESS') DEFAULT 'PERSONAL',
    -- Basic profile
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    bio             TEXT,
    profile_picture VARCHAR(500),
    location        VARCHAR(100),
    website         VARCHAR(200),
    privacy         ENUM('PUBLIC','PRIVATE') DEFAULT 'PUBLIC',
    -- Business/Creator
    business_name   VARCHAR(150),
    category        VARCHAR(100),
    contact_email   VARCHAR(200),
    contact_phone   VARCHAR(20),
    business_address VARCHAR(300),
    business_hours  VARCHAR(200),
    external_links  TEXT COMMENT 'JSON array of links',
    -- Status
    enabled         BOOLEAN DEFAULT TRUE,
    verified        BOOLEAN DEFAULT FALSE,
    -- Timestamps
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ─── Posts ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS posts (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT NOT NULL,
    content              TEXT NOT NULL,
    original_post_id     BIGINT NULL COMMENT 'For reposts',
    type                 ENUM('TEXT','IMAGE','PROMOTIONAL','REPOST','ANNOUNCEMENT') DEFAULT 'TEXT',
    call_to_action_label VARCHAR(50),
    call_to_action_url   VARCHAR(300),
    pinned               BOOLEAN DEFAULT FALSE,
    scheduled_at         DATETIME NULL,
    published            BOOLEAN DEFAULT TRUE,
    view_count           INT DEFAULT 0,
    hashtags             VARCHAR(500),
    image_url            VARCHAR(500),
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (original_post_id) REFERENCES posts(id) ON DELETE SET NULL
);

-- ─── Comments ───────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS comments (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    content    TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ─── Likes ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS likes (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_like (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ─── Connections ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS connections (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id BIGINT NOT NULL,
    addressee_id BIGINT NOT NULL,
    status       ENUM('PENDING','ACCEPTED','REJECTED') DEFAULT 'PENDING',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_connection (requester_id, addressee_id),
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (addressee_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ─── Follows ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS follows (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id  BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_follow (follower_id, following_id),
    FOREIGN KEY (follower_id)  REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ─── Notifications ──────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    sender_id    BIGINT NOT NULL,
    type         ENUM('CONNECTION_REQUEST','CONNECTION_ACCEPTED','NEW_FOLLOWER',
                      'POST_LIKED','POST_COMMENTED','POST_SHARED','POST_MENTIONED') NOT NULL,
    reference_id BIGINT NULL COMMENT 'post_id or comment_id related to notification',
    message      VARCHAR(500),
    is_read      BOOLEAN DEFAULT FALSE,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id)    REFERENCES users(id) ON DELETE CASCADE
);

-- ─── Indexes for Performance ─────────────────────────────────────────────
CREATE INDEX idx_posts_user_id       ON posts(user_id);
CREATE INDEX idx_posts_created_at    ON posts(created_at);
CREATE INDEX idx_posts_hashtags      ON posts(hashtags(100));
CREATE INDEX idx_comments_post_id    ON comments(post_id);
CREATE INDEX idx_likes_post_id       ON likes(post_id);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_id, is_read);
CREATE INDEX idx_connections_users   ON connections(requester_id, addressee_id, status);
CREATE INDEX idx_follows_follower    ON follows(follower_id);
CREATE INDEX idx_follows_following   ON follows(following_id);
