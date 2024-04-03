package com.hidecarbon.hidecarbon.config;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.cluster.nodes}")
    private List<String> clusterNodes;

    @Value("${spring.data.redis.cluster.use}")
    private boolean isCluster;

    public boolean isCluster() {
        return this.isCluster;
    }

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    public String getPassword() {
        return password;
    }
    @Value("${spring.data.redis.password}")
    private String password;
    public int getDataTimeOut() {
        return dataTimeOut;
    }

    @Value("${spring.data.redis.dataTimeOut}")
    private int dataTimeOut;

//    public List<RedisKeyExpiredListener> getKeyExpiredListenerList() {
//        return keyExpiredListenerList;
//    }

    public List<RedisMessageListenerContainer> getRedisMessageListenerContainers() {
        return redisMessageListenerContainers;
    }

//    private List<RedisKeyExpiredListener> keyExpiredListenerList = new ArrayList<>();
    private List<RedisMessageListenerContainer> redisMessageListenerContainers = new ArrayList<>();

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        if(isCluster) {
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();

            if (clusterNodes.size() != 0) {
                clusterNodes.forEach(node -> {
                    String ip = node.substring(0, node.indexOf(":"));
                    int port = Integer.parseInt(node.substring(node.indexOf(":") + 1));
                    log.info("{}:{}", ip, port);
                    clusterConfiguration.clusterNode(ip, port);
                });
            }
            ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder().build();
            ClusterClientOptions clientOptions = ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build();
            LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder().clientOptions(clientOptions).build();

            LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
            connectionFactory.afterPropertiesSet();
            return connectionFactory;

        }
        else {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(host);
            redisStandaloneConfiguration.setPort(port);
            if (password != null) {
                redisStandaloneConfiguration.setPassword(password);
            }
            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
            return lettuceConnectionFactory;

        }
    }

    @Bean
    @Qualifier("reactiveRedisTemplate")
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {

        return new ReactiveRedisTemplate<>(
                redisConnectionFactory,
                RedisSerializationContext.fromSerializer(new GenericJackson2JsonRedisSerializer())
        );
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }
}
