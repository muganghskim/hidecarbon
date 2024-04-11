package com.hidecarbon.hidecarbon.mission.controller;

import com.hidecarbon.hidecarbon.file.service.StorageService;
import com.hidecarbon.hidecarbon.mission.model.MissionDto;
import com.hidecarbon.hidecarbon.mission.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class MissionController {
    private final MissionService missionService;

    private final StorageService storageService;

    @Autowired
    public MissionController(MissionService missionService, StorageService storageService) {
        this.missionService = missionService;
        this.storageService = storageService;
    }

    @GetMapping("/mission/all")
    @Operation(summary = "mission 전체 조회")
    public Page<MissionDto> getMissions(Pageable pageable){
        return missionService.getMissionAll(pageable);
    }

    @PostMapping("/admin/mission/create")
    @Operation(summary = "mission 생성")
    public ResponseEntity<?> createMission(@RequestPart String userEmail,
                                           @RequestPart String title,
                                           @RequestPart String description,
                                           @RequestPart float co2e,
                                           @RequestPart String statDate,
                                           @RequestPart String endDate,
                                           @RequestPart(value = "file") MultipartFile file){
        try {
            // String userEmail, String title, String description, float co2e, String statDate, String endDate, String imgPath
            String imgPath = storageService.storeFile(file);
            missionService.createMission(userEmail, title, description, co2e, statDate, endDate, imgPath);
            return ResponseEntity.ok("200");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
