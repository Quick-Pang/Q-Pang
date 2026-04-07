package com.qpang.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    //데이터를 저장합니다.
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    //유효시간과 함께 데이터를 저장합니다.
    public void setWithTTL(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    //키로 데이터를 조회합니다.
    public Optional<Object> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    //데이터를 삭제합니다.
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    //키의 존재 여부를 확인합니다.
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 현재 값이 expected와 같을 때만 새 값으로 교체하고 TTL을 설정합니다.
    public boolean compareAndSetWithTTL(String key, String expectedValue, Object newValue, Duration duration) {
        String script = """
                if redis.call('GET', KEYS[1]) == ARGV[1] then
                    redis.call('SET', KEYS[1], ARGV[2])
                    redis.call('EXPIRE', KEYS[1], ARGV[3])
                    return 1
                end
                return 0
                """;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                expectedValue,
                String.valueOf(newValue),
                String.valueOf(duration.getSeconds())
        );

        return Long.valueOf(1L).equals(result);
    }
}
