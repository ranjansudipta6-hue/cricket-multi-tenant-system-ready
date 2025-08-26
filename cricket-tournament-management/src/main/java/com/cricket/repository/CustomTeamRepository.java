package com.cricket.repository;

import com.cricket.entity.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomTeamRepository {
    
    @Query("SELECT t FROM Team t WHERE t.id = :id")
    Optional<Team> findTeamById(@Param("id") Long id);
    
    @Query("SELECT t FROM Team t WHERE t.id = :id")
    boolean existsTeamById(@Param("id") Long id);
}
