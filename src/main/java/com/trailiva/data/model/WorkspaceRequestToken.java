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

import static com.trailiva.data.model.TokenType.REFRESH;

@Getter
@Setter
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class WorkspaceRequestToken {
    private final static long EXPIRATION = 48L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(nullable = false, name = "user_id",
            foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    private User contributor;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = User.class)
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

    public WorkspaceRequestToken(String token, User contributor, String tokenType) {
        this.token = token;
        this.contributor = contributor;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public WorkspaceRequestToken(String token, User contributor, String tokenType, OfficialWorkspace workspace) {
        this(token, contributor, tokenType);
        this.workspace = workspace;
    }

    private LocalDateTime calculateExpiryDate(long expiryTimeInHours){
        return LocalDateTime.now().plusHours(expiryTimeInHours);
    }

}
