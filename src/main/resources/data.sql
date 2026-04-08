
-- USERS
INSERT INTO users (
    id, email, password_hash, name,
    onboarding_complete, onboarding_path,
    severity_level, streak_days,
    created_at, updated_at
) VALUES (
             '11111111-1111-1111-1111-111111111111',
             'test@user.com',
             'hashed_password',
             'Test User',
             false,
             'SELF',
             'MILD',
             0,
             CURRENT_TIMESTAMP,
             CURRENT_TIMESTAMP
         );


-- SESSION MODULES
INSERT INTO session_modules (id, name, description, order_index)
VALUES
    ('22222222-2222-2222-2222-222222222222', 'Introduction to CBT', 'Basic CBT concepts', 1),
    ('33333333-3333-3333-3333-333333333333', 'Thought Reframing', 'Learning to reframe thoughts', 2);


-- CBT SESSIONS
INSERT INTO cbt_sessions (
    id, module_id, title, description, duration_minutes, order_index
) VALUES (
             '44444444-4444-4444-4444-444444444444',
             '22222222-2222-2222-2222-222222222222',
             'Understanding Thoughts',
             'Learn how thoughts influence emotions',
             20,
             1
         );

-- COGNITIVE DISTORTIONS
INSERT INTO cognitive_distortions (id, name, description)
VALUES
    ('all-or-nothing', 'All-or-Nothing Thinking', 'Seeing things in extremes'),
    ('catastrophizing', 'Catastrophizing', 'Expecting the worst possible outcome'),
    ('mind-reading', 'Mind Reading', 'Assuming you know what others think'),
    ('overgeneralization', 'Overgeneralization', 'One event defines everything');


-- DISTORTION EXAMPLES
INSERT INTO distortion_examples (distortion_id, example)
VALUES
    ('all-or-nothing', 'If I fail once, I am a total failure'),
    ('catastrophizing', 'If I fail this test, my life is over'),
    ('mind-reading', 'They think I am stupid');


-- TRUSTED CONTACTS
INSERT INTO trusted_contacts (
    id, user_id, name, phone, relationship
) VALUES (
             '55555555-5555-5555-5555-555555555555',
             '11111111-1111-1111-1111-111111111111',
             'Jane Doe',
             '123-456-7890',
             'Friend'
         );