package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class WorkspaceRequestToken extends RepresentationModel<WorkspaceRequestToken> {
    private final static long EXPIRATION = 48L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(nullable = false, name = "user_id",
            foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    private User User;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = OfficialWorkspace.class)
    @JoinColumn(nullable = false, name = "official_workspace_id",
            foreignKey = @ForeignKey(name = "FK_VERIFY_OFFICIAL_WORKSPACE"))
    private OfficialWorkspace workspace;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime datePublished;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDated;

    private LocalDateTime expiryDate;

    private String tokenType;

    public WorkspaceRequestToken(String token, User User, String tokenType) {
        this.token = token;
        this.User = User;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public WorkspaceRequestToken(String token, User User, String tokenType, OfficialWorkspace workspace) {
        this(token, User, tokenType);
        this.workspace = workspace;
    }

    private LocalDateTime calculateExpiryDate(long expiryTimeInHours){
        return LocalDateTime.now().plusHours(expiryTimeInHours);
    }

}
