package com.hidecarbon.hidecarbon.user.service;


import com.hidecarbon.hidecarbon.user.model.MemberDto;
import com.hidecarbon.hidecarbon.redis.repository.CashRepository;
import com.hidecarbon.hidecarbon.user.model.Member;
import com.hidecarbon.hidecarbon.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final CashRepository cashRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, CashRepository cashRepository) {
        this.userRepository = userRepository;
        this.cashRepository = cashRepository;
    }

    /*

        Todo : 캐시 만료처리 완료, 레디스 자동 재연결 로직 완료, 비밀번호는 암호화되어 캐싱됨 -> 토큰을 저장
        동기화: Redis 캐시와 데이터베이스 간의 정보가 동기화되어 있어야 합니다. 사용자 정보가 변경되면 캐시도 업데이트되어야 합니다.
        예를 들어, 사용자의 비밀번호가 변경되거나, 권한이 업데이트되는 경우 캐시에 저장된 정보도 함께 업데이트되어야 합니다.

        캐시 만료: 캐시된 데이터는 영구적이지 않고 일정 기간 후 만료되도록 설정하는 것이 좋습니다.
        이를 통해 오래된 정보가 시스템에 남아있는 것을 방지하고, 데이터의 일관성을 유지할 수 있습니다.

        에러 처리: Redis 서버에 접근하지 못하는 경우(예: 서버 다운, 네트워크 문제 등)를 대비한 에러 처리 로직을 구현하는 것이 중요합니다.
        이는 시스템의 견고성을 높이고, 예기치 않은 오류로부터 시스템을 보호할 수 있습니다.

        보안 고려사항: 사용자 정보(특히 비밀번호와 같은 민감한 정보)를 캐싱할 때는 보안을 충분히 고려해야 합니다. 가능하다면,
        비밀번호는 캐시하지 않고, 필요한 경우에만 데이터베이스에서 직접 조회하는 것이 바람직합니다.

    */
    @Override
    public UserDetails loadUserByUsername(String username) {

        log.info("username: {}", username);
        // Redis에서 사용자 정보 조회 시도
        MemberDto memberCashDto = (MemberDto) cashRepository.get("memberDto::" + username);

        if (memberCashDto != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + memberCashDto.getUserRole()));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(memberCashDto.getUserEmail())
                    .password(memberCashDto.getPassword())
                    .authorities(authorities)
                    .build();
        }
        Optional<Member> userOptional = userRepository.findByUserEmail(username);
        Member member = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 레디스 저장
        MemberDto dto = new MemberDto();
        dto.setUserEmail(member.getUserEmail());
        dto.setUserNo(member.getUserNo());
        dto.setPassword(member.getPassword());
        dto.setUserPhn(member.getUserPhn());
        dto.setImgPath(member.getImgPath());
        dto.setUserName(member.getUserName());
        dto.setUserRole(member.getUserRole());

        cashRepository.save("memberDto::" + username, dto,3, TimeUnit.DAYS);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getUserRole()));

        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getUserEmail())
                .password(member.getPassword())
                .authorities(authorities)
                .build();
    }
}
