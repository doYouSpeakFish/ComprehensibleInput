-- A private, AI-authored plan for each adventure (the setup behind the story) and a private,
-- AI-authored note on each message. Both are context for the narrator only and are never exposed
-- through the API. The note column is left unrestricted by message type so any message could carry
-- one, even though only AI-authored messages are given notes today.
ALTER TABLE text_adventure ADD COLUMN plan TEXT;
ALTER TABLE text_adventure_message ADD COLUMN note TEXT;
