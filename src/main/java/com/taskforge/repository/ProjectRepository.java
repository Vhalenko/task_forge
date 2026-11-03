package com.taskforge.repository;

import com.taskforge.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTeam_IdOrderByIdAsc(Long teamId);
}
