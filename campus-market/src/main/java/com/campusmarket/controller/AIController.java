package com.campusmarket.controller;

import com.campusmarket.common.Result;
import com.campusmarket.dto.AIGenerateDTO;
import com.campusmarket.service.AIApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIApiService aiApiService;

    @PostMapping("/generate-description")
    public Result<String> generateDescription(@Valid @RequestBody AIGenerateDTO dto) {
        String description = aiApiService.generateDescription(dto.getTitle(), dto.getRequirements());
        return Result.success(description);
    }
}
