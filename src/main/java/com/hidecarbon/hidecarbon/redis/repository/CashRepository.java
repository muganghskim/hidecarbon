package com.hidecarbon.hidecarbon.redis.repository;

import com.hidecarbon.hidecarbon.redis.model.AllDtos;
import com.hidecarbon.hidecarbon.redis.model.MemberCashDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class CashRepository {
    @Value("${spring.data.redis.cashKey}")
    private String cashKey;

    @Autowired
    private RedisTemplate<String, Object> template; // ReactiveRedisTemplate 대신 RedisTemplate 사용

    public <T> void save(String reqKey, T content) {
        Map<Long, Object> data = new HashMap<>();
        if (content instanceof MemberCashDto) {
            data.put(((MemberCashDto) content).getUserNo(), content);
        }
        template.opsForHash().put(cashKey, reqKey, data);
        // 전체 해시에 대해 만료 시간 설정
        template.expire(cashKey, 3, TimeUnit.DAYS);
    }

    public Map<Object, Object> findAll() {
        return template.opsForHash().entries(cashKey);
    }

    public Object get(String reqKey) {
        return template.opsForHash().get(cashKey, reqKey);
    }

    public Boolean remove(String reqKey) {
        return template.opsForHash().delete(cashKey, reqKey) > 0;
    }
}
