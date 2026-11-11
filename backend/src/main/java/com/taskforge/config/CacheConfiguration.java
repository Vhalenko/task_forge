package com.taskforge.config;

import com.taskforge.constant.CacheNames;
import com.taskforge.model.dto.ProjectDTO;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.TeamDTO;
import com.taskforge.model.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableCaching
@Profile("!test")
public class CacheConfiguration {

    @Value("${app.cache.ttl}")
    private Duration cacheTtl;

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        RedisCacheConfiguration defaults =
                RedisCacheConfiguration.defaultCacheConfig()
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        );

        JavaType teamType =
                objectMapper.getTypeFactory()
                        .constructType(TeamDTO.class);

        JavaType projectListType =
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, ProjectDTO.class);

        JavaType userListType =
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, UserDTO.class);

        JavaType taskListType =
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, TaskDTO.class);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .withCacheConfiguration(
                        CacheNames.TEAM,
                        jsonCache(objectMapper, teamType)
                )
                .withCacheConfiguration(
                        CacheNames.PROJECTS_BY_TEAM,
                        jsonCache(objectMapper, projectListType)
                )
                .withCacheConfiguration(
                        CacheNames.USERS_BY_TEAM,
                        jsonCache(objectMapper, userListType)
                )
                .withCacheConfiguration(
                        CacheNames.TASKS_BY_PROJECT,
                        jsonCache(objectMapper, taskListType)
                )
                .withCacheConfiguration(
                        CacheNames.TASKS_BY_USER,
                        jsonCache(objectMapper, taskListType)
                )
                .build();
    }

    private RedisCacheConfiguration jsonCache(
            ObjectMapper objectMapper,
            JavaType type
    ) {
        JacksonJsonRedisSerializer<Object> serializer =
                new JacksonJsonRedisSerializer<>(objectMapper, type);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(cacheTtl)
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                );
    }
}