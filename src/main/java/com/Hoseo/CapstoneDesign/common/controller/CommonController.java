package com.Hoseo.CapstoneDesign.common.controller;

import com.Hoseo.CapstoneDesign.common.service.CommonGroupDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/common")
public class CommonController {

    private final CommonGroupDetailService commonGroupDetailService;;

    @GetMapping("/tech-stacks")
    public ResponseEntity<List<String>> getTechStack() {
        //TODO : 공통 테이블에서 테크 스텍 가져오기
        return ResponseEntity.ok(
                List.of("Java","JavaScript","Python","Spring","MySQL","NestJS","React","Vue")
        );
    }

}
