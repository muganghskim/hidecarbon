package com.hidecarbon.hidecarbon.mission.service;

import com.hidecarbon.hidecarbon.mission.model.Mission;
import com.hidecarbon.hidecarbon.mission.model.MissionCompletion;
import com.hidecarbon.hidecarbon.mission.repository.CompletionRepository;
import com.hidecarbon.hidecarbon.mission.repository.MissionRepository;
import com.hidecarbon.hidecarbon.user.model.Member;
import com.hidecarbon.hidecarbon.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class CompletionService {
    private final CompletionRepository completionRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    @Autowired
    public CompletionService(CompletionRepository completionRepository, UserRepository userRepository, MissionRepository missionRepository){
        this.completionRepository = completionRepository;
        this.userRepository = userRepository;
        this.missionRepository = missionRepository;
    }

    /*

        사용자가 미션을 완료하는 과정을 검증하는 것은 무결성을 보장하고 부정 행위를 방지하는 데 중요합니다. 아래는 이를 위해 적용할 수 있는 몇 가지 방법을 제시합니다:

        1. 자동화된 검증 로직 구현
        미션의 성격에 따라 다양한 자동 검증 방법을 구현할 수 있습니다:
        계량적 목표 기반: 예를 들어, 러닝 앱에서는 GPS 데이터를 사용하여 사용자가 정해진 거리를 달렸는지 확인할 수 있습니다.
        사진이나 비디오 제출: 특정 활동(예: 특정 장소 방문, 제품 사용)을 증명하기 위해 사진이나 비디오를 요구할 수 있습니다.
        시간 기반 검증: 미션 완료에 필요한 최소 시간을 설정하여 그 이하로 완료된 경우 검증 절차를 추가로 진행할 수 있습니다.

        2. 제3자 데이터 활용
        외부 API나 서비스를 통해 제공되는 데이터를 사용하여 미션 완료를 검증합니다:
        API 검증: 예를 들어, 운동 미션의 경우 스마트웨어러블 기기의 API에서 제공하는 데이터를 사용하여 걸음 수나 운동 시간을 검증할 수 있습니다.
        협력 업체 데이터 활용: 예를 들어, 미션 완료가 상점 방문을 요구할 경우, POS 시스템의 데이터를 통해 확인할 수 있습니다.

        3. 커뮤니티 기반 검증
        사용자 커뮤니티의 도움을 받아 미션 완료를 검증하는 방법입니다:
        피어 리뷰: 다른 사용자가 제출된 증거(사진, 비디오 등)를 검토하여 미션의 유효성을 평가할 수 있습니다.
        포인트 시스템: 미션 완료 후 사용자 평가를 포인트 시스템과 연동하여 보상과 페널티를 부여할 수 있습니다.

        4. 변조 방지 기술
        데이터의 변조를 방지하기 위한 기술을 적용할 수 있습니다:
        블록체인 기술: 데이터의 무결성을 보장하기 위해 블록체인을 활용하여 미션 완료 데이터를 저장하고 검증할 수 있습니다.
        디지털 서명: 사용자가 제출한 증거의 변경을 감지하기 위해 디지털 서명 기술을 사용할 수 있습니다.

        5. 감사 로그
        모든 미션 완료 및 관련 활동에 대해 상세한 감사 로그를 유지하여 이상 징후를 조기에 탐지할 수 있습니다. 이는 부정 행위 시도를 추적하고 분석하는 데 유용합니다.

        미션 완료 과정을 검증하는 방식은 미션의 유형, 기술 인프라, 타깃 사용자의 행동 등 다양한 요인에 따라 달라질 수 있습니다.
        따라서 목적에 가장 적합한 검증 방법을 선택하고, 가능하다면 여러 방법을 조합하여 사용하는 것이 효과적일 수 있습니다.

    */

    // 사용자 미션 참여
    @Transactional
    public void createMissionCompletion(String userEmail, Long missionNo){

        Optional<Member> memberOpt = userRepository.findByUserEmail(userEmail);

        if (!memberOpt.isPresent()) {
            throw new RuntimeException("회원을 찾을 수 없습니다.");
        }

        Optional<Mission> missionOpt = missionRepository.findById(missionNo);

        if (!missionOpt.isPresent()) {
            throw new RuntimeException("미션을 찾을 수 없습니다.");
        }

        MissionCompletion missionCompletion = MissionCompletion.builder()
                .normalMember(memberOpt.get())
                .mission(missionOpt.get())
                .status("진행중")
                .build();

        completionRepository.save(missionCompletion);
    }

    // Todo : 사용자 미션들 조회, 검증 및 업데이트
}
