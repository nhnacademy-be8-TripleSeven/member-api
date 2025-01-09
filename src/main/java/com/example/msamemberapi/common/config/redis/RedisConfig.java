package com.example.msamemberapi.common.config.redis;

import com.example.msamemberapi.application.dto.response.CartDto;
import com.example.msamemberapi.application.dto.response.CartDto.CartItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, CartDto> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CartDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(CartItem.class));
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(CartItem.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
