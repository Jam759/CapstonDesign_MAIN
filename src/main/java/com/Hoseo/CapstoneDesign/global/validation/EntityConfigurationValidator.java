package com.Hoseo.CapstoneDesign.global.validation;

import com.Hoseo.CapstoneDesign.global.entity.interfaces.CustomSoftDeletable;
import com.Hoseo.CapstoneDesign.global.validation.exception.EntityConfigurationException;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityConfigurationValidator implements ApplicationListener<ContextRefreshedEvent> {

    private final EntityManagerFactory emf;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        emf.getMetamodel().getEntities().forEach(entityType -> {
            Class<?> clazz = entityType.getJavaType();
            if (CustomSoftDeletable.class.isAssignableFrom(clazz)) {
                SQLDelete sqlDelete = clazz.getAnnotation(SQLDelete.class);
                if (sqlDelete == null) {
                    throw new EntityConfigurationException(clazz, "@SQLDelete 설정 누락");
                }
            }
        });
    }
}
