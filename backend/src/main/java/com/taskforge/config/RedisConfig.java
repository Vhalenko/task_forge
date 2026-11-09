package com.taskforge.config;

import com.taskforge.model.dto.TeamDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
@Profile("!test")
public class RedisConfig {

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        JacksonJsonRedisSerializer<TeamDTO> teamSerializer =
                new JacksonJsonRedisSerializer<>(TeamDTO.class);

        RedisCacheConfiguration teamConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(teamSerializer)
                        );

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("teamCache", teamConfig)
                .build();
    }
}
