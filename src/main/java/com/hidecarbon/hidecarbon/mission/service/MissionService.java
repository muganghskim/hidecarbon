package com.hidecarbon.hidecarbon.mission.service;

import com.hidecarbon.hidecarbon.mission.model.Mission;
import com.hidecarbon.hidecarbon.mission.model.MissionDto;
import com.hidecarbon.hidecarbon.mission.repository.MissionRepository;
import com.hidecarbon.hidecarbon.redis.repository.CashRepository;
import com.hidecarbon.hidecarbon.user.model.Member;
import com.hidecarbon.hidecarbon.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MissionService {

    private final UserRepository userRepository;

    private final MissionRepository missionRepository;

    private final CashRepository cashRepository;

    @Autowired
    public MissionService(UserRepository userRepository, MissionRepository missionRepository, CashRepository cashRepository){
        this.userRepository = userRepository;
        this.missionRepository = missionRepository;
        this.cashRepository = cashRepository;
    }

    // 미션 생성
    // Todo : start end 변경
    @Transactional
    public void createMission(String userEmail, String title, String description, float co2e, String statDate, String endDate, String imgPath){
        Optional<Member> memberOpt = userRepository.findByUserEmail(userEmail);

        if (!memberOpt.isPresent()) {
            throw new RuntimeException("회원을 찾을 수 없습니다.");
        }
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();

        Mission mission = Mission.builder()
                .adminMember(memberOpt.get())
                .title(title)
                .description(description)
                .co2e(co2e)
                .startDate(start)
                .endDate(end)
                .imgPath(imgPath)
                .build();

        missionRepository.save(mission);

        // 캐시 무효화
        cashRepository.remove("getMissionAll");
    }

    /*

        캐시 업데이트 전략
        캐시 무효화 (Cache Invalidation):
        변경이 일어날 때 캐시를 완전히 지우는 방법입니다. 이는 가장 간단하지만, 데이터가 자주 업데이트되는 경우 성능에 부정적인 영향을 미칠 수 있습니다. 캐시 무효화를 통해 데이터 일관성을 유지할 수 있습니다.

        캐시 업데이트 (Cache Update):
        변경이 일어날 때 캐시된 데이터를 직접 업데이트하는 방법입니다. 이 방법은 캐시 무효화보다 복잡할 수 있지만, 데이터가 자주 업데이트되고, 해당 캐시 데이터에 대한 요청이 많은 경우 성능을 크게 향상시킬 수 있습니다.

        캐시 세그먼트화 (Cache Segmentation):
        데이터를 세그먼트(예: 페이지 번호와 같은 키를 포함)로 분할하여 캐시합니다. 이렇게 하면 특정 세그먼트만 무효화하거나 업데이트할 수 있어, 전체 데이터를 캐싱하는 것보다 더 세밀한 제어가 가능합니다.

    */

    // 미션 조회
    // Todo : Page 객체를 때려 박았으나 실제 될지는 테스트 해봐야할듯...
    @Transactional(readOnly = true)
    public Page<MissionDto> getMissionAll(Pageable pageable){

        String missionsKey = "getMissionAll";
        Page<MissionDto> cachedMissions = (Page<MissionDto>) cashRepository.get(missionsKey); // 캐시에서 데이터 조회

        if (cachedMissions != null) {
            return cachedMissions; // 캐시된 데이터가 있으면 반환
        }

        Page<Mission> missions = missionRepository.findAll(pageable);
        Page<MissionDto> missionDtos = missions.map(entity -> {
            MissionDto dto = new MissionDto();
            dto.setMissionNo(entity.getMissionNo());
            dto.setTitle(entity.getTitle());
            dto.setCo2e(entity.getCo2e());
//            dto.setUserNo(entity.getAdminMember().getUserNo());
            dto.setStartDate(entity.getStartDate());
            dto.setEndDate(entity.getEndDate());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setUpdatedAt(entity.getUpdatedAt());
            return dto;
        });

        cashRepository.save(missionsKey, missionDtos, 3, TimeUnit.DAYS); // 캐시에 데이터 저장
        return missionDtos;
    }

    // 미션 하나 조회
    @Transactional(readOnly = true)
    public MissionDto getMission(Long missionNo){

        String missionKey = "getMission::" + missionNo;
        MissionDto cashedMission = (MissionDto) cashRepository.get(missionKey);

        if(cashedMission != null) {
            return cashedMission;
        }

        Optional<Mission> missionOpt = missionRepository.findById(missionNo);
        if (!missionOpt.isPresent()) {
            throw new RuntimeException("미션을 찾을 수 없습니다.");
        }
        Mission mission = missionOpt.get();
        MissionDto dto = new MissionDto();
        dto.setMissionNo(mission.getMissionNo());
        dto.setTitle(mission.getTitle());
        dto.setDescription(mission.getDescription());
        dto.setCo2e(mission.getCo2e());
        dto.setStartDate(mission.getStartDate());
        dto.setEndDate(mission.getEndDate());
        dto.setCreatedAt(mission.getCreatedAt());
        dto.setUpdatedAt(mission.getUpdatedAt());
        dto.setImgPath(mission.getImgPath());

        cashRepository.save(missionKey, dto, 3, TimeUnit.DAYS);

        return dto;

    }

    // 미션 업데이트
    @Transactional
    public void updateMission(Long missionNo, String title, String description, float co2e, String statDate, String endDate, String imgPath){
        // Todo : startDate 와 endDate 정의
        String missionKey = "getMission::" + missionNo;
        LocalDateTime updateDate = LocalDateTime.now();
        Optional<Mission> missionOpt = missionRepository.findById(missionNo);
        if (!missionOpt.isPresent()) {
            throw new RuntimeException("미션을 찾을 수 없습니다.");
        }
        Mission mission = missionOpt.get();
        mission.setTitle(title);
        mission.setDescription(description);
        mission.setCo2e(co2e);
        mission.setStartDate(updateDate);
        mission.setEndDate(updateDate);
        mission.setUpdatedAt(updateDate);
        mission.setImgPath(imgPath);

        missionRepository.save(mission);

        // 캐시 초기화
        cashRepository.remove(missionKey);

        MissionDto dto = new MissionDto();
        dto.setMissionNo(mission.getMissionNo());
        dto.setTitle(mission.getTitle());
        dto.setDescription(mission.getDescription());
        dto.setCo2e(mission.getCo2e());
        dto.setStartDate(mission.getStartDate());
        dto.setEndDate(mission.getEndDate());
        dto.setCreatedAt(mission.getCreatedAt());
        dto.setUpdatedAt(mission.getUpdatedAt());
        dto.setImgPath(mission.getImgPath());

        cashRepository.save(missionKey, dto, 3, TimeUnit.DAYS);
    }
}
