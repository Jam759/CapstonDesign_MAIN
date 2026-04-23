package com.Hoseo.CapstoneDesign.friend.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendException extends GlobalBaseException {

    private final FriendErrorCode errorCode;

    @Override
    public GlobalErrorCode getErrorCode() {
        return this.errorCode;
    }
}
