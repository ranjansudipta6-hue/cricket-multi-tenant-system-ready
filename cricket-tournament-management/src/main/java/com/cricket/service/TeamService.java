package com.cricket.service;

import com.cricket.entity.Team;
import com.cricket.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepository repo;

    public TeamService(TeamRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Team save(Team t) {
        return repo.save(t);
    }

    @Transactional(readOnly = true)
    public List<Team> all() {
        return repo.findAll();
    }
}
