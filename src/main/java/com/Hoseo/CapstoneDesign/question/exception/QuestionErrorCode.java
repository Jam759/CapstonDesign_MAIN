package com.Hoseo.CapstoneDesign.question.exception;

import com.Hoseo.CapstoneDesign.global.exception.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum QuestionErrorCode implements GlobalErrorCode {

    // 404 Not Found 에러
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "해당 질문을 찾을 수 없습니다."),
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, 4002, "해당 답변을 찾을 수 없습니다."),

    // 403 Forbidden 에러 (본인 글이 아닌데 수정/삭제하려 할 때)
    QUESTION_ACCESS_DENIED(HttpStatus.FORBIDDEN, 4003, "해당 질문에 대한 권한이 없습니다."),
    ANSWER_ACCESS_DENIED(HttpStatus.FORBIDDEN, 4004, "해당 답변에 대한 권한이 없습니다."),

    // 400 Bad Request 에러 (잘못된 요청)
    INVALID_QUESTION_REQUEST(HttpStatus.BAD_REQUEST, 4005, "잘못된 질문 형식입니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;
}