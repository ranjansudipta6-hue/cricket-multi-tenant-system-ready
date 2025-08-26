package com.cricket.controller;

import com.cricket.entity.Player;
import com.cricket.entity.Team;
import com.cricket.repository.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerRepository repo;

    public PlayerController(PlayerRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Player> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Player> get(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid player ID: " + id);
            }
            Player player = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found with id: " + id));
            return ResponseEntity.ok(player);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error retrieving player: " + e.getMessage());
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Player> create(@RequestBody Player p) {
        try {
            if (p.getFullName() == null || p.getFullName().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player full name is required");
            }
            
            // Handle team relationship properly
            if (p.getTeam() != null && p.getTeam().getId() != null) {
                // Create a new Team object with just the ID to avoid detached entity issues
                Team team = new Team();
                team.setId(p.getTeam().getId());
                p.setTeam(team);
            }
            
            Player savedPlayer = repo.save(p);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error creating player: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Player> update(@PathVariable Long id, @RequestBody Player p) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid player ID: " + id);
            }
            if (p.getFullName() == null || p.getFullName().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player full name is required");
            }
            
            // Check if player exists first
            Player existingPlayer = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found with id: " + id));
            
            // Update the existing player
            existingPlayer.setFullName(p.getFullName().trim());
            existingPlayer.setRole(p.getRole() != null ? p.getRole().trim() : null);
            
            // Handle team relationship properly
            if (p.getTeam() != null && p.getTeam().getId() != null) {
                Team team = new Team();
                team.setId(p.getTeam().getId());
                existingPlayer.setTeam(team);
            }
            
            Player updatedPlayer = repo.save(existingPlayer);
            return ResponseEntity.ok(updatedPlayer);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error updating player: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid player ID: " + id);
            }
            
            // Check if player exists first
            if (!repo.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found with id: " + id);
            }
            
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error deleting player: " + e.getMessage());
        }
    }
}
