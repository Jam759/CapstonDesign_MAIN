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

    private final CommonGroupDetailService commonGroupDetailService;

    @GetMapping("/tech-stacks")
    public ResponseEntity<List<String>> getTechStacks() {
        return ResponseEntity.ok(commonGroupDetailService.getProjectTechStackIds());
    }

    @GetMapping("/positions")
    public ResponseEntity<List<String>> getPositions() {
        return ResponseEntity.ok(commonGroupDetailService.getProjectPositionIds());
    }

    @GetMapping("/goals")
    public ResponseEntity<List<String>> getGoals() {
        return ResponseEntity.ok(commonGroupDetailService.getUserGoalIds());
    }

}
