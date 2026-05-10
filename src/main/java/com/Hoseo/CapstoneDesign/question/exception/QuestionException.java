package com.Hoseo.CapstoneDesign.question.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalBaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionException extends GlobalBaseException {
    private final QuestionErrorCode errorCode;
}