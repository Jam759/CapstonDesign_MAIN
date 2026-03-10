package com.Hoseo.CapstoneDesign.global.bootGuard;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.global.bootGuard.exception.FacadeTransactionalGuardException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Slf4j
@Component
public class FacadeTransactionalGuard implements SmartInitializingSingleton {

    private final ApplicationContext applicationContext;

    public FacadeTransactionalGuard(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            String[] beanNames = applicationContext.getBeanDefinitionNames();

            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);
                Class<?> targetClass = AopUtils.getTargetClass(bean);

                if (!AnnotatedElementUtils.hasAnnotation(targetClass, Facade.class)) {
                    continue;
                }

                for (Method method : targetClass.getDeclaredMethods()) {
                    if (!isCandidateMethod(method)) {
                        continue;
                    }

                    boolean hasMethodTransactional =
                            AnnotatedElementUtils.hasAnnotation(method, Transactional.class);

                    if (!hasMethodTransactional) {
                        throw new FacadeTransactionalGuardException(
                                targetClass,
                                method,
                                "@Facade 클래스의 public 메서드에는 반드시 @Transactional 이 선언되어야 합니다."
                        );
                    }
                }
            }
            log.info("[FacadeTransactionalGuard] Validate Success! ");
        } catch (FacadeTransactionalGuardException e) {
            throw e;
        } catch (Exception e) {
            throw new FacadeTransactionalGuardException(
                    "부팅 중 @Facade 트랜잭션 정책 검사에서 예기치 못한 예외가 발생했습니다.",
                    e
            );
        }
    }

    private boolean isCandidateMethod(Method method) {
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            return false;
        }
        if (method.isSynthetic() || method.isBridge()) {
            return false;
        }
        return method.getDeclaringClass() != Object.class;
    }
}