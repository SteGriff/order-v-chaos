CREATE TABLE battles (
    id SERIAL PRIMARY KEY,
    theme_id INT NOT NULL REFERENCES themes(id),
    left_final_score INT NOT NULL DEFAULT 0,
    right_final_score INT NOT NULL DEFAULT 0,
    started TIMESTAMP NOT NULL,
    ended TIMESTAMP NULL
);

CREATE INDEX idx_battles_ended ON battles(ended);
CREATE INDEX idx_battles_theme_id ON battles(theme_id);
