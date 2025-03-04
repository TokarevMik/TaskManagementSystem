package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private String email;
    private String password;

    @Column(name = "user_name")
    private String userName;

    @OneToMany(mappedBy = "author_id")
    List<Task> cratedTasks;

    @OneToMany(mappedBy = "executor_id")
    List<Task> submittedTasks;

    @OneToMany(mappedBy = "user")
    List<Comment> commentsList;

    @OneToMany(mappedBy = "author")
    private List<Task> authoredTasks;

    @OneToMany(mappedBy = "executor")
    private List<Task> assignedTasks;

    @ElementCollection(targetClass = RoleType.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name="user_id"))
    @Column(name="roles", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<RoleType> roleType = new HashSet<>();


}
