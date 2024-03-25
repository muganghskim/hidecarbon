package com.hidecarbon.hidecarbon.mission.model;

import com.hidecarbon.hidecarbon.user.model.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "MissionCompletion")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long completeNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userNo")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="missionNo")
    private Mission mission;

    private LocalDateTime completeDate;

    private float gainCo2e;

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
    public MissionCompletion(Member member, Mission mission, LocalDateTime completeDate, float gainCo2e) {
        this.member = member;
        this.mission = mission;
        this.completeDate = completeDate;
        this.gainCo2e = gainCo2e;
    }
}
