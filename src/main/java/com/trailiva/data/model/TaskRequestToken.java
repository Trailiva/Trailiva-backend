package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.trailiva.util.AppConstants.EXPIRATION;


@Getter
@Setter
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class TaskRequestToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(nullable = false, name = "user_id",
            foreignKey = @ForeignKey(name = "FK_USER"))
    private User user;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = Task.class)
    @JoinColumn(nullable = false, name = "task_id",
            foreignKey = @ForeignKey(name = "FK_TASK"))
    private Task task;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime datePublished;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDated;

    private LocalDateTime expiryDate;

    private String tokenType;

    public TaskRequestToken(String token, User user, String tokenType) {
        this.token = token;
        this.user = user;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public TaskRequestToken(String token, User user, String tokenType, Task task) {
        this(token, user, tokenType);
        this.task = task;
    }

    private LocalDateTime calculateExpiryDate(long expiryTimeInHours){
        return LocalDateTime.now().plusHours(expiryTimeInHours);
    }
}
