package com.hidecarbon.hidecarbon.carboncredit.model;

import com.hidecarbon.hidecarbon.user.model.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "CarbonCredit")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CarbonCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carbonCreditNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userNo")
    private Member member;

    private float co2e;

    private LocalDateTime expireTime;

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
    public CarbonCredit(Member member, float co2e, LocalDateTime expireTime) {
        this.member = member;
        this.co2e = co2e;
        this.expireTime = expireTime;
    }
}
