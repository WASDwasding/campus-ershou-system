package com.campus.ershou.ai;

import com.campus.ershou.ai.dto.GoodsDraftRequest;
import com.campus.ershou.ai.dto.QuoteRequest;
import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.common.web.ClientIp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/api/ai/goods-draft")
    public ApiResponse<Map<String, Object>> draft(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody GoodsDraftRequest request
    ) {
        return ApiResponse.ok(aiService.draft(loginUser.userId(), request.imageFileIds(), request.prompt()));
    }

    @PostMapping("/api/ai/quotes")
    public ApiResponse<Map<String, Object>> quote(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody QuoteRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(aiService.quote(loginUser.userId(), request.goodsId(), ClientIp.resolve(httpRequest)));
    }
}
