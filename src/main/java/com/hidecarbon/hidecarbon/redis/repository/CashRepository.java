package com.hidecarbon.hidecarbon.redis.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.hidecarbon.hidecarbon.mission.model.MissionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class CashRepository {
    @Autowired
    private RedisTemplate<String, Object> template;

    // 데이터 저장 및 만료 시간 설정
    public <T> void save(String reqKey, T content, long timeout, TimeUnit timeUnit) {
        // 데이터를 JSON으로 직렬화하거나, RedisTemplate 설정에 따라 자동으로 직렬화될 수 있음
        template.opsForValue().set(reqKey, content, timeout, timeUnit);
    }

    // 데이터 조회
    public Object get(String reqKey) {
        return template.opsForValue().get(reqKey);
    }

    // 데이터 삭제
    public Boolean remove(String reqKey) {
        return template.delete(reqKey);
    }
}
