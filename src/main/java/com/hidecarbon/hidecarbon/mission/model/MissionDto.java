package com.hidecarbon.hidecarbon.mission.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MissionDto {

    private Long missionNo;

//    private Long userNo;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private float co2e;

    private String imgPath;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
