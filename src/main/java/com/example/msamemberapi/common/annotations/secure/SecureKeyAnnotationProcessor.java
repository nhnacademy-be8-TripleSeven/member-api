package com.example.msamemberapi.common.annotations.secure;

import com.example.msamemberapi.application.service.impl.SecureKeyManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
@RequiredArgsConstructor
public class SecureKeyAnnotationProcessor implements BeanPostProcessor {

    private final Environment environment; // Spring Environment 객체
    private final SecureKeyManagerService secureKeyManagerService; // SKM 연동 서비스

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(SecureKey.class)) {
                SecureKey annotation = field.getAnnotation(SecureKey.class);
                String envPath = annotation.value(); // 환경변수 경로
                String key = environment.getProperty(envPath); // 환경변수에서 키 가져오기

                if (key == null) {
                    throw new IllegalArgumentException("No key found in environment for path: " + envPath);
                }

                String value = secureKeyManagerService.fetchSecretFromKeyManager(key);

                if (value == null) {
                    throw new IllegalArgumentException("No value found in SKM for key: " + key);
                }

                try {
                    field.setAccessible(true);
                    field.set(bean, value); // 필드에 값을 설정
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject secure key: " + key, e);
                }
            }
        }
        return bean;
    }
}
