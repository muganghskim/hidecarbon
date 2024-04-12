package com.hidecarbon.hidecarbon.mission.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class MissionCompletionDto {
    private Long completeNo;
    private String title;
    private LocalDateTime completeDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
