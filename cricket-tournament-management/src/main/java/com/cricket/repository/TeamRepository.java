package com.cricket.repository;

import com.cricket.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    
    // Use bulk query approach that works with multi-tenant routing
    default Optional<Team> findTeamById(Long id) {
        return findAll().stream()
                .filter(team -> team.getId().equals(id))
                .findFirst();
    }
    
    default boolean existsTeamById(Long id) {
        return findAll().stream()
                .anyMatch(team -> team.getId().equals(id));
    }
}
