package com.Hoseo.CapstoneDesign.global.bootGuard;

import com.Hoseo.CapstoneDesign.global.bootGuard.exception.EntityConfigurationException;
import com.Hoseo.CapstoneDesign.global.entity.interfaces.CustomSoftDeletable;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityConfigurationGuard implements ApplicationListener<ContextRefreshedEvent> {

    private final EntityManagerFactory emf;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        emf.getMetamodel().getEntities().forEach(entityType -> {
            Class<?> clazz = entityType.getJavaType();
            if (CustomSoftDeletable.class.isAssignableFrom(clazz)) {
                SQLDelete sqlDelete = clazz.getAnnotation(SQLDelete.class);
                if (sqlDelete == null) {
                    throw new EntityConfigurationException(clazz, "@SQLDelete configuration missing");
                }

                SQLRestriction sqlRestriction = clazz.getAnnotation(SQLRestriction.class);
                if (sqlRestriction == null) {
                    throw new EntityConfigurationException(clazz, "@SQLRestriction configuration missing");
                }
            }
        });
    }
}
