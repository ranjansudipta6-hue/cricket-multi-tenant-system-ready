package com.cricket.repository;

import com.cricket.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    long countByHomeTeam_IdOrAwayTeam_Id(Long homeTeamId, Long awayTeamId);
    List<Match> findByHomeTeam_IdOrAwayTeam_Id(Long homeTeamId, Long awayTeamId);
}
