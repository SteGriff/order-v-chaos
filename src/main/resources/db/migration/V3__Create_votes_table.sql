CREATE TABLE votes (
    id BIGSERIAL PRIMARY KEY,
    battle_id INT NOT NULL REFERENCES battles(id),
    is_left BOOLEAN NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_votes_battle_id ON votes(battle_id);
CREATE INDEX idx_votes_created ON votes(created);
