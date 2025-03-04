package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name = "task")
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID uuid;

    @Column(name = "task_name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status", columnDefinition = "ENUM('NEW', 'PENDING','IN_PROGRESS','COMPLETED')")
    private Status status = Status.NEW;

    @Column(name = "priority", columnDefinition = "ENUM('LOW','MIDDLE', 'HIGH')")
    private TaskPriority taskPriority = TaskPriority.MIDDLE;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;


}
