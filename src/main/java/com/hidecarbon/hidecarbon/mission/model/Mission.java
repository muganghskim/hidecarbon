package com.hidecarbon.hidecarbon.mission.model;

import com.hidecarbon.hidecarbon.user.model.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "Mission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long missionNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userNo")
    private Member member;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private float co2e;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // PrePersist is used before the very first time the object is inserted into the database.
    // This will set both createdAt and updatedAt timestamps to the current time when a new entity is created.
    @PrePersist
    protected void onCreate() {
        this.createdAt= LocalDateTime.now();
        this.updatedAt= LocalDateTime.now();
    }

    // PreUpdate is used before any update on the data occurs,
    // so every time an update happens on that row updatedAt will be set to that current timestamp.
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt= LocalDateTime.now();
    }

    @Builder
    public Mission(Member member, String title, String description, LocalDateTime startDate, LocalDateTime endDate, float co2e) {
        this.member = member;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.co2e = co2e;
    }
}
