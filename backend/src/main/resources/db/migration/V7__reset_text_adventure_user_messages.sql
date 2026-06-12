-- User messages previously stored raw text in both sentence languages.
-- They now use AI-structured paragraphs. Clear all adventure data so old
-- messages with incorrect structure are not loaded.
DELETE FROM text_adventure;
