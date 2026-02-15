CREATE TABLE themes (
    id SERIAL PRIMARY KEY,
    left_side VARCHAR(100) NOT NULL,
    right_side VARCHAR(100) NOT NULL,
    left_colour VARCHAR(7) NOT NULL,
    right_colour VARCHAR(7) NOT NULL,
    left_emoji VARCHAR(10) NOT NULL,
    right_emoji VARCHAR(10) NOT NULL,
    ordinal INT NOT NULL
);

CREATE INDEX idx_themes_ordinal ON themes(ordinal);
