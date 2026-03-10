package com.Hoseo.CapstoneDesign.global.bootGuard.exception;

import java.lang.reflect.Method;

public class FacadeTransactionalGuardException extends RuntimeException {

    public FacadeTransactionalGuardException(Class<?> facadeClass, Method method, String reason) {
        super(buildMessage(facadeClass, method, reason));
    }

    public FacadeTransactionalGuardException(Class<?> facadeClass, Method method, String reason, Throwable cause) {
        super(buildMessage(facadeClass, method, reason), cause);
    }

    public FacadeTransactionalGuardException(String reason, Throwable cause) {
        super("[BOOT_MESSAGE] FacadeTransactionalGuard : " + reason, cause);
    }

    private static String buildMessage(Class<?> facadeClass, Method method, String reason) {
        return "[BOOT_MESSAGE] FacadeTransactionalGuard : ["
                + facadeClass.getSimpleName()
                + "] ["
                + method.getName()
                + "] -> "
                + reason;
    }
}