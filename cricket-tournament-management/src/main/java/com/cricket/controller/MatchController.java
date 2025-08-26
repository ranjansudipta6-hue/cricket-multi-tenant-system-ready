package com.cricket.controller;

import com.cricket.entity.Match;
import com.cricket.entity.Team;
import com.cricket.entity.Tournament;
import com.cricket.repository.MatchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/matches")
public class MatchController {
    private final MatchRepository repo;

    public MatchController(MatchRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Match> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Match> get(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid match ID: " + id);
            }
            Match match = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found with id: " + id));
            return ResponseEntity.ok(match);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error retrieving match: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Match> create(@RequestBody Match m) {
        try {
            if (m.getVenue() == null || m.getVenue().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Match venue is required");
            }
            
            // Handle tournament relationship properly
            if (m.getTournament() != null && m.getTournament().getId() != null) {
                Tournament tournament = new Tournament();
                tournament.setId(m.getTournament().getId());
                m.setTournament(tournament);
            }
            
            // Handle home team relationship properly
            if (m.getHomeTeam() != null && m.getHomeTeam().getId() != null) {
                Team homeTeam = new Team();
                homeTeam.setId(m.getHomeTeam().getId());
                m.setHomeTeam(homeTeam);
            }
            
            // Handle away team relationship properly
            if (m.getAwayTeam() != null && m.getAwayTeam().getId() != null) {
                Team awayTeam = new Team();
                awayTeam.setId(m.getAwayTeam().getId());
                m.setAwayTeam(awayTeam);
            }
            
            Match savedMatch = repo.save(m);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMatch);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error creating match: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> update(@PathVariable Long id, @RequestBody Match m) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid match ID: " + id);
            }
            if (m.getVenue() == null || m.getVenue().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Match venue is required");
            }
            
            // Check if match exists first
            Match existingMatch = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found with id: " + id));
            
            // Update the existing match
            existingMatch.setVenue(m.getVenue().trim());
            existingMatch.setMatchDate(m.getMatchDate());
            existingMatch.setHomeScore(m.getHomeScore());
            existingMatch.setAwayScore(m.getAwayScore());
            
            // Handle tournament relationship properly
            if (m.getTournament() != null && m.getTournament().getId() != null) {
                Tournament tournament = new Tournament();
                tournament.setId(m.getTournament().getId());
                existingMatch.setTournament(tournament);
            }
            
            // Handle home team relationship properly
            if (m.getHomeTeam() != null && m.getHomeTeam().getId() != null) {
                Team homeTeam = new Team();
                homeTeam.setId(m.getHomeTeam().getId());
                existingMatch.setHomeTeam(homeTeam);
            }
            
            // Handle away team relationship properly
            if (m.getAwayTeam() != null && m.getAwayTeam().getId() != null) {
                Team awayTeam = new Team();
                awayTeam.setId(m.getAwayTeam().getId());
                existingMatch.setAwayTeam(awayTeam);
            }
            
            Match updatedMatch = repo.save(existingMatch);
            return ResponseEntity.ok(updatedMatch);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error updating match: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid match ID: " + id);
            }
            
            // Check if match exists first
            if (!repo.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found with id: " + id);
            }
            
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error deleting match: " + e.getMessage());
        }
    }
}
