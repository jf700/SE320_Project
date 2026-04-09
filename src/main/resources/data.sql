-- ─── Cognitive Distortions ───────────────────────────────────────────────────
MERGE INTO cognitive_distortions(id, name, description) KEY(id) VALUES
('all-or-nothing',     'All-or-Nothing Thinking',  'Seeing things in black-and-white categories with no middle ground.'),
('catastrophizing',    'Catastrophizing',           'Expecting the worst possible outcome without considering other possibilities.'),
('mind-reading',       'Mind Reading',              'Assuming you know what others are thinking without evidence.'),
('overgeneralization', 'Overgeneralization',        'Making broad conclusions based on a single event.'),
('should-statements',  'Should Statements',         'Using rigid rules about how you or others should behave.'),
('mental-filtering',   'Mental Filtering',          'Focusing exclusively on negatives while ignoring positives.'),
('fortune-telling',    'Fortune Telling',           'Predicting the future will turn out badly as though it is fact.'),
('emotional-reasoning','Emotional Reasoning',       'Assuming that because you feel something, it must be true.'),
('labeling',           'Labeling',                  'Attaching a negative label to yourself or others based on one event.'),
('personalization',    'Personalization',           'Blaming yourself for things that are not entirely your fault.');

-- ─── Distortion Examples ─────────────────────────────────────────────────────
MERGE INTO distortion_examples(distortion_id, example) KEY(distortion_id, example) VALUES
('all-or-nothing',     'If I am not perfect, I am a total failure.'),
('all-or-nothing',     'Either I do it right or not at all.'),
('catastrophizing',    'If I make a mistake, I will definitely get fired.'),
('catastrophizing',    'This headache must be a brain tumor.'),
('mind-reading',       'They must think I am incompetent.'),
('mind-reading',       'She did not smile because she hates me.'),
('overgeneralization', 'I failed once, so I will always fail.'),
('overgeneralization', 'Nobody ever listens to me.'),
('should-statements',  'I should always be productive.'),
('should-statements',  'They should know how I feel.'),
('mental-filtering',   'I got great feedback but my manager pointed out one flaw — I am terrible.'),
('fortune-telling',    'I know this presentation will go badly.'),
('emotional-reasoning','I feel like a failure, so I must be one.'),
('labeling',           'I made a mistake, so I am a loser.'),
('personalization',    'My team missed the deadline — it is all my fault.');

-- ─── Session Modules ─────────────────────────────────────────────────────────
MERGE INTO session_modules(id, name, description, order_index) KEY(id) VALUES
('11111111-1111-1111-1111-111111111101', 'Foundations of CBT',
    'Learn the core principles of Cognitive Behavioral Therapy and how thoughts affect feelings.', 1),
('11111111-1111-1111-1111-111111111102', 'Identifying Cognitive Distortions',
    'Recognize the common thinking traps that contribute to stress and burnout.', 2),
('11111111-1111-1111-1111-111111111103', 'Thought Challenging',
    'Learn to examine and reframe unhelpful automatic thoughts using evidence.', 3),
('11111111-1111-1111-1111-111111111104', 'Behavioral Activation',
    'Break the cycle of avoidance and withdrawal through planned positive activities.', 4),
('11111111-1111-1111-1111-111111111105', 'Burnout Recovery',
    'Specific strategies for recovering from workplace burnout using CBT principles.', 5);

-- ─── CBT Sessions ────────────────────────────────────────────────────────────
MERGE INTO cbt_sessions(id, module_id, title, description, duration_minutes, order_index) KEY(id) VALUES
('22222222-2222-2222-2222-222222222201', '11111111-1111-1111-1111-111111111101',
    'Introduction to CBT', 'Understand the CBT model: thoughts, feelings, and behaviors are interconnected.', 20, 1),
('22222222-2222-2222-2222-222222222202', '11111111-1111-1111-1111-111111111101',
    'The Thought-Feeling Connection', 'Explore how automatic thoughts influence your emotional state.', 25, 2),
('22222222-2222-2222-2222-222222222203', '11111111-1111-1111-1111-111111111102',
    'All-or-Nothing Thinking', 'Identify and challenge black-and-white thinking patterns.', 30, 1),
('22222222-2222-2222-2222-222222222204', '11111111-1111-1111-1111-111111111102',
    'Catastrophizing & Mind Reading', 'Recognize future-predicting and mind-reading distortions.', 30, 2),
('22222222-2222-2222-2222-222222222205', '11111111-1111-1111-1111-111111111103',
    'The Thought Record', 'Use a structured thought record to challenge unhelpful beliefs.', 35, 1),
('22222222-2222-2222-2222-222222222206', '11111111-1111-1111-1111-111111111103',
    'Finding Alternative Thoughts', 'Develop balanced, evidence-based alternative perspectives.', 30, 2),
('22222222-2222-2222-2222-222222222207', '11111111-1111-1111-1111-111111111104',
    'Activity Scheduling', 'Plan meaningful activities to counteract low mood and withdrawal.', 25, 1),
('22222222-2222-2222-2222-222222222208', '11111111-1111-1111-1111-111111111105',
    'Understanding Burnout', 'Explore the three dimensions of burnout and your recovery path.', 30, 1),
('22222222-2222-2222-2222-222222222209', '11111111-1111-1111-1111-111111111105',
    'Setting Boundaries at Work', 'Practical CBT-based strategies for sustainable boundaries.', 35, 2),
('22222222-2222-2222-2222-222222222210', '11111111-1111-1111-1111-111111111105',
    'Values and Meaningful Work', 'Reconnect with your core values to guide recovery decisions.', 40, 3);

-- ─── Session Objectives ───────────────────────────────────────────────────────
MERGE INTO cbt_session_objectives(session_id, objective) KEY(session_id, objective) VALUES
('22222222-2222-2222-2222-222222222201', 'Understand the CBT triangle'),
('22222222-2222-2222-2222-222222222201', 'Identify one automatic thought from this week'),
('22222222-2222-2222-2222-222222222205', 'Complete a full 7-column thought record'),
('22222222-2222-2222-2222-222222222205', 'Rate belief before and after challenging the thought'),
('22222222-2222-2222-2222-222222222208', 'Score yourself on the Maslach Burnout Inventory dimensions'),
('22222222-2222-2222-2222-222222222208', 'Identify your primary burnout driver');

-- ─── Session Modalities ───────────────────────────────────────────────────────
MERGE INTO cbt_session_modalities(session_id, modality) KEY(session_id, modality) VALUES
('22222222-2222-2222-2222-222222222201', 'TEXT'),
('22222222-2222-2222-2222-222222222202', 'TEXT'),
('22222222-2222-2222-2222-222222222203', 'TEXT'),
('22222222-2222-2222-2222-222222222204', 'TEXT'),
('22222222-2222-2222-2222-222222222205', 'TEXT'),
('22222222-2222-2222-2222-222222222206', 'TEXT'),
('22222222-2222-2222-2222-222222222207', 'TEXT'),
('22222222-2222-2222-2222-222222222208', 'TEXT'),
('22222222-2222-2222-2222-222222222209', 'TEXT'),
('22222222-2222-2222-2222-222222222210', 'TEXT');
