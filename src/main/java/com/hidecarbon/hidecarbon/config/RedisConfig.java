package com.hidecarbon.hidecarbon.config;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import java.time.Duration;
import java.util.List;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.cluster.nodes}")
    private List<String> clusterNodes;

    @Value("${spring.data.redis.cluster.use}")
    private boolean isCluster;

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2)) // 커맨드 타임아웃 설정
                .shutdownTimeout(Duration.ZERO) // 셧다운 타임아웃 설정
                .clientOptions(ClusterClientOptions.builder()
                        .autoReconnect(true) // 자동 재연결 활성화
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT) // 연결 끊김 시 동작 설정
                        .build())
                .build();

        if (isCluster) {
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(clusterNodes);
            return new LettuceConnectionFactory(clusterConfiguration, clientConfig);
        } else {
            RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
            standaloneConfig.setHostName(host);
            standaloneConfig.setPort(port);
            standaloneConfig.setPassword(password);
            return new LettuceConnectionFactory(standaloneConfig, clientConfig);
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
