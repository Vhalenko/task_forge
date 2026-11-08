package com.taskforge.model.entity;

import com.taskforge.model.dto.TeamDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Project> projects;

    @OneToMany(mappedBy = "team")
    private List<User> users;

    public TeamDTO toDto() {
        return new TeamDTO(
                id,
                name
        );
    }
}
