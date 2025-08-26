package com.cricket.controller;

import com.cricket.entity.Team;
import com.cricket.entity.Match;
import com.cricket.repository.TeamRepository;
import com.cricket.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamRepository repo;
    private final Optional<MatchRepository> matchRepo;

    public TeamController(TeamRepository repo, @Autowired(required = false) MatchRepository matchRepo) {
        this.repo = repo;
        this.matchRepo = Optional.ofNullable(matchRepo);
    }

    @GetMapping
    public ResponseEntity<?> all(@RequestParam(name = "id", required = false) Long id) {
        try {
            List<Team> allTeams = repo.findAll();
            
            // If id parameter is provided, return single team
            if (id != null) {
                if (id <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid team ID: " + id);
                }
                Team team = allTeams.stream()
                        .filter(t -> t.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
                return ResponseEntity.ok(team);
            }
            
            // Return all teams
            return ResponseEntity.ok(allTeams);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error retrieving teams: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getOne(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid team ID: " + id);
            }

            // Use working bulk operation to find individual record
            List<Team> allTeams = repo.findAll();
            Team team = allTeams.stream()
                    .filter(t -> t.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));

            return ResponseEntity.ok(team);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error retrieving team: " + e.getMessage());
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Team> create(@RequestBody Team t) {
        try {
            if (t.getName() == null || t.getName().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name is required");
            }
            Team savedTeam = repo.save(t);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTeam);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating team: " + e.getMessage());
        }
    }

    @GetMapping("/by-id")
    public ResponseEntity<Team> getById(@RequestParam(name = "id") Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid team ID: " + id);
            }
            
            // Use working bulk operation to find individual record
            List<Team> allTeams = repo.findAll();
            Team team = allTeams.stream()
                    .filter(t -> t.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
            
            return ResponseEntity.ok(team);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error retrieving team: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Team> update(@PathVariable("id") Long id, @RequestBody Team t) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid team ID: " + id);
            }
            if (t.getName() == null || t.getName().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name is required");
            }
            
            // Check if team exists first using working bulk operation
            List<Team> allTeams = repo.findAll();
            Team existingTeam = allTeams.stream()
                    .filter(team -> team.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
            
            // Update the existing team
            existingTeam.setName(t.getName().trim());
            existingTeam.setCity(t.getCity() != null ? t.getCity().trim() : null);
            
            Team updatedTeam = repo.save(existingTeam);
            return ResponseEntity.ok(updatedTeam);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error updating team: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid team ID: " + id);
            }
            
            // Handle referencing matches by deleting them first (cascade delete)
            if (matchRepo.isPresent()) {
                // Find and delete all matches that reference this team
                List<Match> referencingMatches = matchRepo.get().findByHomeTeam_IdOrAwayTeam_Id(id, id);
                if (!referencingMatches.isEmpty()) {
                    matchRepo.get().deleteAll(referencingMatches);
                    // Log the cascade deletion for transparency
                    System.out.println("Cascade deleted " + referencingMatches.size() + " match(es) that referenced team " + id);
                }
            }
            
            // First check if team exists using working bulk operation
            List<Team> allTeams = repo.findAll();
            Team team = allTeams.stream()
                    .filter(teamItem -> teamItem.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
            
            // Clear the players collection (this will delete them due to orphanRemoval = true)
            if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
                team.getPlayers().clear();
                repo.save(team); // Save to apply orphan removal
            }
            
            // Now delete the team
            repo.deleteById(id);
            
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error deleting team: " + e.getMessage());
        }
    }
}