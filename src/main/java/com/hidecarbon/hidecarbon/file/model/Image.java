package com.hidecarbon.hidecarbon.file.model;

import com.hidecarbon.hidecarbon.user.model.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "Image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageNo;

    private String entityType; // "mission", "reward", "member" 등의 값을 가질 수 있음

    private Long entityId; // entityType에 따라 missionNo, rewardNo, userNo 중 하나의 값을 가짐

    private String imgPath;

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
    public Image(String entityType, Long entityId, String imgPath) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.imgPath = imgPath;
    }
}
