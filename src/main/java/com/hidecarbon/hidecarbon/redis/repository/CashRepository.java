package com.hidecarbon.hidecarbon.redis.repository;

import com.hidecarbon.hidecarbon.redis.model.AllDtos;
import com.hidecarbon.hidecarbon.redis.model.MemberCashDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Repository
public class CashRepository {
    @Value("${spring.data.redis.cashKey}")
    private String cashKey;

    @Autowired
    @Qualifier("reactiveRedisTemplate")
    private ReactiveRedisTemplate<Object, Object> template;

    public <T extends AllDtos> Mono<?> save(String reqKey, T content) {
        Map<Long, Object> data = new HashMap<>();
        if (content instanceof MemberCashDto) {
            data.put(((MemberCashDto) content).getUserNo(), content);
        }
        return template.opsForHash().put(cashKey, reqKey, data);
    }

    public Flux<Map.Entry<Object, Object>> findAll() {
        return template.opsForHash().scan(cashKey, ScanOptions.scanOptions().build());
    }

    public Mono<Object> get(String reqKey) {
        return template.opsForHash().get(cashKey, reqKey);
    }

    public Mono<Boolean> remove(String reqKey) {
        return template.opsForHash().remove(cashKey, reqKey).hasElement();
    }
}
