
-- USERS
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       name VARCHAR(255),
                       onboarding_complete BOOLEAN,
                       onboarding_path VARCHAR(50),
                       severity_level VARCHAR(50),
                       streak_days INT,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP
);


-- SESSION MODULES
CREATE TABLE session_modules (
                                 id UUID PRIMARY KEY,
                                 name VARCHAR(255),
                                 description TEXT,
                                 order_index INT
);


-- CBT SESSIONS
CREATE TABLE cbt_sessions (
                              id UUID PRIMARY KEY,
                              module_id UUID,
                              title VARCHAR(255),
                              description TEXT,
                              duration_minutes INT,
                              order_index INT,
                              CONSTRAINT fk_module FOREIGN KEY (module_id) REFERENCES session_modules(id)
);


-- USER SESSIONS
CREATE TABLE user_sessions (
                               id UUID PRIMARY KEY,
                               user_id UUID,
                               cbt_session_id UUID,
                               status VARCHAR(50),
                               started_at TIMESTAMP,
                               ended_at TIMESTAMP,
                               mood_before INT,
                               mood_after INT,
                               CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
                               CONSTRAINT fk_cbt FOREIGN KEY (cbt_session_id) REFERENCES cbt_sessions(id)
);


-- DIARY ENTRIES
CREATE TABLE diary_entries (
                               id UUID PRIMARY KEY,
                               user_id UUID,
                               situation TEXT,
                               automatic_thought TEXT,
                               alternative_thought TEXT,
                               mood_before INT,
                               mood_after INT,
                               belief_rating_before INT,
                               belief_rating_after INT,
                               created_at TIMESTAMP,
                               deleted BOOLEAN,
                               CONSTRAINT fk_diary_user FOREIGN KEY (user_id) REFERENCES users(id)
);


-- CHAT MESSAGES
CREATE TABLE chat_messages (
                               id UUID PRIMARY KEY,
                               user_session_id UUID,
                               role VARCHAR(50),
                               content TEXT,
                               modality VARCHAR(50),
                               timestamp TIMESTAMP,
                               CONSTRAINT fk_chat_session FOREIGN KEY (user_session_id) REFERENCES user_sessions(id)
);


-- TRUSTED CONTACTS
CREATE TABLE trusted_contacts (
                                  id UUID PRIMARY KEY,
                                  user_id UUID,
                                  name VARCHAR(255),
                                  phone VARCHAR(50),
                                  relationship VARCHAR(100),
                                  CONSTRAINT fk_contact_user FOREIGN KEY (user_id) REFERENCES users(id)
);


-- DIARY ENTRY DISTORTIONS (JOIN TABLE)
CREATE TABLE diary_entry_distortions (
                                         entry_id UUID,
                                         distortion_id VARCHAR(100)
);


-- DIARY ENTRY EMOTIONS (ELEMENT COLLECTION)
CREATE TABLE diary_entry_emotions (
                                      entry_id UUID,
                                      emotion VARCHAR(255)
);


-- CBT SESSION OBJECTIVES
CREATE TABLE cbt_session_objectives (
                                        session_id UUID,
                                        objective VARCHAR(255)
);


-- CBT SESSION MODALITIES
CREATE TABLE cbt_session_modalities (
                                        session_id UUID,
                                        modality VARCHAR(50)
);


-- DISTORTION EXAMPLES
CREATE TABLE distortion_examples (
                                     distortion_id VARCHAR(100),
                                     example VARCHAR(255)
);


-- COGNITIVE DISTORTIONS
CREATE TABLE cognitive_distortions (
                                       id VARCHAR(100) PRIMARY KEY,
                                       name VARCHAR(255),
                                       description TEXT
);