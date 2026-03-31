package com.Hoseo.CapstoneDesign.global.logging;

import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@Order(100)
public class TraceLoggingAspect {

    private static final int MAX_VALUE_LEN = 800;
    private static final int MAX_COLLECTION_ITEMS = 30;

    @Around("""
(
    execution(* com.Hoseo.CapstoneDesign..controller..*(..)) ||
    execution(* com.Hoseo.CapstoneDesign..service..*(..)) ||
    execution(* com.Hoseo.CapstoneDesign..repository..*(..)) ||
    within(com.Hoseo.CapstoneDesign..*Controller*) ||
    within(com.Hoseo.CapstoneDesign..*Service*) ||
    within(com.Hoseo.CapstoneDesign..*Repository*) ||
    within(com.Hoseo.CapstoneDesign..*Factory*) ||
    within(com.Hoseo.CapstoneDesign..*Util*) ||
    within(com.Hoseo.CapstoneDesign..*Facade*)
)
&&
!within(com.Hoseo.CapstoneDesign..config..*) &&
!within(com.Hoseo.CapstoneDesign..properties..*) &&
!within(com.Hoseo.CapstoneDesign..*Configuration*) &&
!within(com.Hoseo.CapstoneDesign..*Properties*)
""")
    public Object trace(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();

        String className = sig.getDeclaringType().getSimpleName();
        String methodName = method.getName();
        String fullName = className + "." + methodName;

        String argsStr = formatArgs(sig.getParameterNames(), pjp.getArgs());

        Instant start = Instant.now();
        log.info("[TRACE_IN] {} args={}", fullName, argsStr);

        try {
            Object result = pjp.proceed();
            long ms = Duration.between(start, Instant.now()).toMillis();

            String resultStr = formatValue(result);
            log.info("[TRACE_OUT] {} timeMs={} result={}", fullName, ms, resultStr);
            return result;
        } catch (Throwable t) {
            long ms = Duration.between(start, Instant.now()).toMillis();
            log.error(
                    "[TRACE_ERR] {} timeMs={} exType={} msg={}",
                    fullName,
                    ms,
                    t.getClass().getName(),
                    safeTruncate(String.valueOf(t.getMessage())),
                    t
            );
            throw t;
        }
    }

    private String formatArgs(String[] paramNames, Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        List<String> parts = new ArrayList<>(args.length);
        for (int i = 0; i < args.length; i++) {
            String name = (paramNames != null && i < paramNames.length) ? paramNames[i] : ("arg" + i);
            parts.add(name + "=" + formatValue(args[i]));
        }
        return safeTruncate(parts.toString());
    }

    private String formatValue(Object v) {
        if (v == null) {
            return "null";
        }

        Class<?> clazz = v.getClass();
        String className = clazz.getName();

        // JPA Entity / Hibernate Proxy / PersistentCollection 은 절대 toString() 호출 금지
        if (isHibernateProxy(v)) {
            return "<hibernate-proxy:" + getUserClassName(v) + ">";
        }

        if (isPersistentCollection(v)) {
            return "<persistent-collection:" + clazz.getSimpleName() + ">";
        }

        if (isJpaEntity(v)) {
            return "<entity:" + clazz.getSimpleName() + ">";
        }

        // 너무 무거운 타입은 타입만 표시
        if (className.startsWith("jakarta.servlet.")
                || className.startsWith("org.springframework.web.")
                || className.contains("HttpServletRequest")
                || className.contains("HttpServletResponse")
                || className.contains("MultipartFile")
                || className.contains("InputStream")
                || className.contains("OutputStream")) {
            return "<" + clazz.getSimpleName() + ">";
        }

        if (v instanceof CharSequence s) {
            return "\"" + safeTruncate(s.toString()) + "\"";
        }

        if (v instanceof Number || v instanceof Boolean || v instanceof Enum<?>) {
            return safeTruncate(String.valueOf(v));
        }

        if (v instanceof UUID || v instanceof Date) {
            return safeTruncate(String.valueOf(v));
        }

        if (clazz.isArray()) {
            int len = java.lang.reflect.Array.getLength(v);
            int take = Math.min(len, MAX_COLLECTION_ITEMS);
            List<String> arr = new ArrayList<>(take);

            for (int i = 0; i < take; i++) {
                Object item = java.lang.reflect.Array.get(v, i);
                arr.add(formatValue(item));
            }

            String body = "len=" + len + " " + arr;
            return safeTruncate("<array " + body + ">");
        }

        if (v instanceof Collection<?> c) {
            int size = c.size();
            List<String> sample = c.stream()
                    .limit(MAX_COLLECTION_ITEMS)
                    .map(this::formatValue)
                    .toList();

            String body = "size=" + size + " " + sample;
            return safeTruncate("<collection " + body + ">");
        }

        if (v instanceof Map<?, ?> m) {
            int size = m.size();
            List<String> sample = new ArrayList<>();
            int i = 0;

            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (i++ >= MAX_COLLECTION_ITEMS) {
                    break;
                }
                sample.add(formatValue(e.getKey()) + ":" + formatValue(e.getValue()));
            }

            String body = "size=" + size + " {" + String.join(", ", sample) + "}";
            return safeTruncate("<map " + body + ">");
        }

        // 기본: toString 허용 타입만
        return safeTruncate(String.valueOf(v));
    }

    private boolean isJpaEntity(Object v) {
        return getUserClass(v).isAnnotationPresent(Entity.class);
    }

    private boolean isHibernateProxy(Object v) {
        return v instanceof HibernateProxy;
    }

    private boolean isPersistentCollection(Object v) {
        return v instanceof PersistentCollection;
    }

    private Class<?> getUserClass(Object v) {
        if (v instanceof HibernateProxy proxy) {
            return proxy.getHibernateLazyInitializer().getPersistentClass();
        }
        return v.getClass();
    }

    private String getUserClassName(Object v) {
        return getUserClass(v).getSimpleName();
    }

    private static String safeTruncate(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() <= MAX_VALUE_LEN) {
            return s;
        }

        int head = MAX_VALUE_LEN / 2;
        int tail = MAX_VALUE_LEN - head - 20;
        if (tail < 0) {
            tail = 0;
        }

        return s.substring(0, head)
                + " ...<truncated>... "
                + s.substring(Math.max(s.length() - tail, head));
    }
}