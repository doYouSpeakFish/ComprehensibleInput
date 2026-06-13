-- The CEFR difficulty level (e.g. "B1") an adventure is written at. Chosen when the adventure is
-- started and fed back into the prompt for every later turn so the difficulty stays consistent.
-- Existing adventures default to B1 to preserve the behaviour from before levels were selectable.
ALTER TABLE text_adventure ADD COLUMN language_level VARCHAR(8) NOT NULL DEFAULT 'B1';
