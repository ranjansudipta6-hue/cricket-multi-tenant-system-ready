INSERT INTO teams(id, name, city) VALUES (1, 'Mumbai Mavericks','Mumbai');
INSERT INTO teams(id, name, city) VALUES (2, 'Chennai Chargers','Chennai');

INSERT INTO players(id, full_name, role, team_id) VALUES (1, 'Rohit Kumar','Batsman', 1);
INSERT INTO players(id, full_name, role, team_id) VALUES (2, 'Arun Iyer','Bowler', 1);
INSERT INTO players(id, full_name, role, team_id) VALUES (3, 'Sanjay Nair','All-rounder', 2);

INSERT INTO tenants (tenant_id, jndi_name, db_url, db_username, db_password, driver_class, active) VALUES
  ('tenant1', NULL, 'jdbc:oracle:thin:@localhost:1521:orcl', 'user1', 'pass1', 'oracle.jdbc.OracleDriver', 1);

INSERT INTO tenants (tenant_id, jndi_name, db_url, db_username, db_password, driver_class, active) VALUES
  ('tenant2', NULL, 'jdbc:oracle:thin:@localhost:1521:orcl', 'user2', 'pass2', 'oracle.jdbc.OracleDriver', 1);

INSERT INTO tenants (tenant_id, jndi_name, db_url, db_username, db_password, driver_class, active) VALUES
  ('tenant3', 'java:comp/env/jdbc/tenant3', NULL, NULL, NULL, NULL, 0);

INSERT INTO tournaments(id, name, year) VALUES (1, 'IPL', 2024);
INSERT INTO tournaments(id, name, year) VALUES (2, 'Champions Trophy', 2025);

INSERT INTO matches(id, tournament_id, home_team_id, away_team_id, match_date, venue, home_score, away_score) VALUES
  (1, 1, 1, 2, TO_DATE('2024-04-10', 'YYYY-MM-DD'), 'Wankhede Stadium', 180, 175);
INSERT INTO matches(id, tournament_id, home_team_id, away_team_id, match_date, venue, home_score, away_score) VALUES
  (2, 2, 2, 1, TO_DATE('2025-05-15', 'YYYY-MM-DD'), 'Chidambaram Stadium', 150, 160);