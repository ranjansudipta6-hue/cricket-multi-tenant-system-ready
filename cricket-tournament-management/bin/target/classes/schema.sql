CREATE TABLE IF NOT EXISTS teams (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  city VARCHAR(200)
);
CREATE TABLE IF NOT EXISTS players (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(200) NOT NULL,
  role VARCHAR(50),
  team_id BIGINT REFERENCES teams(id)
);
CREATE TABLE IF NOT EXISTS tournaments (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  year INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS matches (
  id BIGSERIAL PRIMARY KEY,
  tournament_id BIGINT REFERENCES tournaments(id),
  home_team_id BIGINT REFERENCES teams(id),
  away_team_id BIGINT REFERENCES teams(id),
  match_date DATE,
  venue VARCHAR(200),
  home_score INTEGER,
  away_score INTEGER
);
