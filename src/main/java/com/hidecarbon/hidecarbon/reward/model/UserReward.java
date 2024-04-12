package com.hidecarbon.hidecarbon.reward.model;

import com.hidecarbon.hidecarbon.user.model.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "UserReward")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRewardNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userNo")
    private Member normalMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="rewardNo")
    private Reward reward;

    private LocalDateTime rewardDate;

    private String status;

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
    public UserReward(Member normalMember, Reward reward, LocalDateTime rewardDate, String status) {
        this.normalMember = normalMember;
        this.reward = reward;
        this.rewardDate = rewardDate;
        this.status = status;
    }

}
