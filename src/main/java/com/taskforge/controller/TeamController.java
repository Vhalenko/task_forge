package com.taskforge.controller;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.TeamDTO;
import com.taskforge.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<TeamDTO>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<TeamDTO> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teamService.findById(id));
    }
}
