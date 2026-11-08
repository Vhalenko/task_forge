package com.taskforge.repository;

import com.taskforge.model.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject_IdOrderByIdAsc(Long projectId);

    @EntityGraph(attributePaths = {
            "project",
            "createdBy",
            "assignees"
    })
    List<Task> findDistinctByAssignees_IdOrderByIdAsc(Long userId);
}
