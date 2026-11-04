package com.taskforge.support;

import com.taskforge.model.entity.Comment;
import com.taskforge.model.entity.Project;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.Team;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.TaskPriority;
import com.taskforge.model.enums.TaskStatus;
import com.taskforge.model.enums.UserRole;
import com.taskforge.repository.CommentRepository;
import com.taskforge.repository.ProjectRepository;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.TeamRepository;
import com.taskforge.repository.UserRepository;
import com.taskforge.security.CustomUserDetails;
import com.taskforge.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestSupport {

    protected static final String DEFAULT_PASSWORD = "secret123";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TeamRepository teamRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected TaskRepository taskRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    protected Team createTeam(String name) {
        return teamRepository.save(Team.builder().name(name).build());
    }

    protected User createUser(String firstName,
                              String lastName,
                              String email,
                              UserRole role,
                              Team team,
                              boolean blocked) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(firstName.toLowerCase() + "." + lastName.toLowerCase())
                .email(email)
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(role)
                .team(team)
                .build();
        user.setBlocked(blocked);
        return userRepository.save(user);
    }

    protected User createUser(String email) {
        return createUser("Test", "User", email, UserRole.USER, null, false);
    }

    protected User createAdmin(String email) {
        return createUser("Admin", "User", email, UserRole.ADMIN, null, false);
    }

    protected Project createProject(Team team, String name) {
        return projectRepository.save(Project.builder().name(name).team(team).build());
    }

    protected Task createTask(User creator, Project project, String title) {
        return createTask(creator, project, title, TaskStatus.TODO, TaskPriority.MEDIUM);
    }

    protected Task createTask(User creator,
                              Project project,
                              String title,
                              TaskStatus status,
                              TaskPriority priority) {
        return taskRepository.save(Task.builder()
                .title(title)
                .description("Test task description")
                .status(status)
                .priority(priority)
                .project(project)
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .build());
    }

    protected Comment createComment(Task task, User user, String text, LocalDateTime createdAt) {
        return commentRepository.save(Comment.builder()
                .text(text)
                .task(task)
                .user(user)
                .createdAt(createdAt)
                .build());
    }

    /** Returns a raw JWT (without the "Bearer " prefix). */
    protected String tokenFor(User user) {
        return jwtService.generateToken(new CustomUserDetails(user));
    }

    protected String bearer(User user) {
        return "Bearer " + tokenFor(user);
    }

    protected String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not serialize test payload", exception);
        }
    }
}
