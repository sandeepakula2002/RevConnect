-- ============================================================
-- Sample Data for RevConnect P2 Demonstration
-- ============================================================

-- First, clear existing data (order matters for foreign keys)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE follows;
TRUNCATE TABLE connections;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- Insert Sample Users
-- ============================================================
INSERT INTO users (
    id, username, email, first_name, last_name, 
    bio, role, privacy, created_at, updated_at,
    enabled, verified, password
) VALUES 
(1, 'alexchen', 'alex.chen@email.com', 'Alex', 'Chen', 
 'Software developer & tech enthusiast', 'PERSONAL', 'PUBLIC', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword'),

(2, 'sarahjohnson', 'sarah.j@email.com', 'Sarah', 'Johnson', 
 'Digital marketer | Helping brands grow', 'PERSONAL', 'PUBLIC', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword'),

(3, 'mikebrown', 'mike.brown@email.com', 'Mike', 'Brown', 
 'Travel photographer | Exploring the world', 'PERSONAL', 'PRIVATE', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword'),

(4, 'emilydavis', 'emily.davis@email.com', 'Emily', 'Davis', 
 'Content creator | Fashion & lifestyle', 'CREATOR', 'PUBLIC', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword'),

(5, 'techcorp', 'info@techcorp.com', 'Tech', 'Corp', 
 'Leading provider of tech solutions', 'BUSINESS', 'PUBLIC', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword'),

(6, 'davidwilson', 'david.w@email.com', 'David', 'Wilson', 
 'Fitness trainer | Helping you achieve your goals', 'PERSONAL', 'PUBLIC', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword'),

(7, 'lisagarcia', 'lisa.garcia@email.com', 'Lisa', 'Garcia', 
 'Food blogger & recipe developer', 'CREATOR', 'PUBLIC', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword'),

(8, 'jamesroberts', 'james.r@email.com', 'James', 'Roberts', 
 'Music producer | Creating beats since 2010', 'PERSONAL', 'PRIVATE', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword'),

(9, 'greenearth', 'contact@greenearth.org', 'Green', 'Earth', 
 'Non-profit environmental organization', 'BUSINESS', 'PUBLIC', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword'),

(10, 'ninapatel', 'nina.p@email.com', 'Nina', 'Patel', 
 'Wellness coach | Yoga & meditation', 'CREATOR', 'PUBLIC', NOW(), NOW(),
 true, false, '$2a$10$dummyhashforpassword');

-- ============================================================
-- Insert Sample Connections
-- ============================================================

-- ACCEPTED connections
INSERT INTO connections (id, requester_id, addressee_id, status, created_at) VALUES
(1, 1, 2, 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 1, 3, 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, 2, 3, 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 4, 1, 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(5, 5, 2, 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(6, 6, 1, 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(7, 7, 3, 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(8, 8, 4, 'ACCEPTED', DATE_SUB(NOW(), INTERVAL 6 DAY));

-- PENDING requests received
INSERT INTO connections (id, requester_id, addressee_id, status, created_at) VALUES
(9, 9, 1, 'PENDING', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(10, 10, 1, 'PENDING', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(11, 5, 3, 'PENDING', DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(12, 8, 2, 'PENDING', DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- PENDING requests sent
INSERT INTO connections (id, requester_id, addressee_id, status, created_at) VALUES
(13, 1, 7, 'PENDING', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(14, 2, 8, 'PENDING', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(15, 3, 9, 'PENDING', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ============================================================
-- Insert Sample Follows
-- ============================================================
INSERT INTO follows (id, follower_id, following_id, created_at) VALUES
(1, 1, 4, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(2, 1, 5, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(3, 1, 7, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, 2, 4, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(5, 2, 10, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(6, 3, 5, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(7, 3, 9, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(8, 4, 1, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(9, 4, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(10, 5, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(11, 6, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(12, 6, 3, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(13, 7, 1, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(14, 8, 4, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(15, 9, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(16, 10, 3, DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- ============================================================
-- Reset auto-increment counters
-- ============================================================
ALTER TABLE users AUTO_INCREMENT = 11;
ALTER TABLE connections AUTO_INCREMENT = 16;
ALTER TABLE follows AUTO_INCREMENT = 17;