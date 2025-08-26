package com.cricket.controller;

import com.cricket.entity.Tournament;
import com.cricket.repository.TournamentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    private final TournamentRepository repo;

    public TournamentController(TournamentRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Tournament> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Tournament> get(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid tournament ID: " + id);
            }
            Tournament tournament = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found with id: " + id));
            return ResponseEntity.ok(tournament);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error retrieving tournament: " + e.getMessage());
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Tournament> create(@RequestBody Tournament t) {
        try {
            if (t.getName() == null || t.getName().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tournament name is required");
            }
            if (t.getYear() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid tournament year is required");
            }
            Tournament savedTournament = repo.save(t);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTournament);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating tournament: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(@PathVariable Long id, @RequestBody Tournament t) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid tournament ID: " + id);
            }
            if (t.getName() == null || t.getName().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tournament name is required");
            }
            if (t.getYear() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid tournament year is required");
            }
            
            // Check if tournament exists first
            Tournament existingTournament = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found with id: " + id));
            
            // Update the existing tournament
            existingTournament.setName(t.getName().trim());
            existingTournament.setYear(t.getYear());
            
            Tournament updatedTournament = repo.save(existingTournament);
            return ResponseEntity.ok(updatedTournament);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error updating tournament: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid tournament ID: " + id);
            }
            
            // Check if tournament exists first
            if (!repo.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found with id: " + id);
            }
            
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error deleting tournament: " + e.getMessage());
        }
    }
}
