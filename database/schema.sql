CREATE DATABASE IF NOT EXISTS revconnect_db;

USE revconnect_db;

-- USERS TABLE
CREATE TABLE users (

    id INT AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(50) UNIQUE NOT NULL,

    email VARCHAR(100) UNIQUE NOT NULL,

    password VARCHAR(255) NOT NULL,

    role VARCHAR(20) DEFAULT 'USER',

    bio TEXT,

    location VARCHAR(100),

    website VARCHAR(200),

    profile_picture VARCHAR(255),

    is_private BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);



-- POSTS TABLE

CREATE TABLE posts (

    id INT AUTO_INCREMENT PRIMARY KEY,

    user_id INT,

    content TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)

);



-- COMMENTS TABLE

CREATE TABLE comments (

    id INT AUTO_INCREMENT PRIMARY KEY,

    user_id INT,

    post_id INT,

    comment TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),

    FOREIGN KEY (post_id) REFERENCES posts(id)

);



-- LIKES TABLE

CREATE TABLE likes (

    id INT AUTO_INCREMENT PRIMARY KEY,

    user_id INT,

    post_id INT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),

    FOREIGN KEY (post_id) REFERENCES posts(id)

);



-- CONNECTION REQUESTS

CREATE TABLE connections (

    id INT AUTO_INCREMENT PRIMARY KEY,

    sender_id INT,

    receiver_id INT,

    status VARCHAR(20),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (sender_id) REFERENCES users(id),

    FOREIGN KEY (receiver_id) REFERENCES users(id)

);



-- FOLLOW SYSTEM

CREATE TABLE follows (

    id INT AUTO_INCREMENT PRIMARY KEY,

    follower_id INT,

    following_id INT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (follower_id) REFERENCES users(id),

    FOREIGN KEY (following_id) REFERENCES users(id)

);



-- NOTIFICATIONS TABLE

CREATE TABLE notifications (

    id INT AUTO_INCREMENT PRIMARY KEY,

    user_id INT,

    message VARCHAR(255),

    type VARCHAR(50),

    is_read BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)

);



-- HASHTAGS TABLE

CREATE TABLE hashtags (

    id INT AUTO_INCREMENT PRIMARY KEY,

    tag VARCHAR(50) UNIQUE

);



-- POST HASHTAGS

CREATE TABLE post_hashtags (

    id INT AUTO_INCREMENT PRIMARY KEY,

    post_id INT,

    hashtag_id INT,

    FOREIGN KEY (post_id) REFERENCES posts(id),

    FOREIGN KEY (hashtag_id) REFERENCES hashtags(id)

);