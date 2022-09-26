package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.trailiva.util.AppConstants.EXPIRATION;


@Getter
@Setter
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class ProjectRequestToken extends RepresentationModel<ProjectRequestToken> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(nullable = false, name = "user_id",
            foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    private User User;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = Project.class)
    @JoinColumn(nullable = false, name = "project_id",
            foreignKey = @ForeignKey(name = "FK_PROJECT"))
    private Project project;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime datePublished;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDated;

    private LocalDateTime expiryDate;

    private String tokenType;

    public ProjectRequestToken(String token, User User, String tokenType) {
        this.token = token;
        this.User = User;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public ProjectRequestToken(String token, User User, String tokenType, Project project) {
        this(token, User, tokenType);
        this.project = project;
    }

    private LocalDateTime calculateExpiryDate(long expiryTimeInHours){
        return LocalDateTime.now().plusHours(expiryTimeInHours);
    }
}
