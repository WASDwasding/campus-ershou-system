package com.campus.ershou.im;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.security.LoginUser;
import com.campus.ershou.im.dto.OpenConversationRequest;
import com.campus.ershou.im.dto.ReadConversationRequest;
import com.campus.ershou.im.dto.SendMessageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ImController {

    private final ConversationService conversationService;

    public ImController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/api/im/conversations")
    public ApiResponse<Map<String, Object>> open(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody OpenConversationRequest request
    ) {
        if (request.goodsId() == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("goodsId", "请选择商品")));
        }
        return ApiResponse.ok(conversationService.open(loginUser.userId(), request.goodsId()));
    }

    @GetMapping("/api/im/conversations")
    public ApiResponse<Map<String, Object>> list(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(conversationService.list(loginUser.userId(), page, size));
    }

    @GetMapping("/api/im/conversations/{id}")
    public ApiResponse<Map<String, Object>> detail(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long id
    ) {
        return ApiResponse.ok(conversationService.detail(loginUser.userId(), id));
    }

    @GetMapping("/api/im/conversations/{id}/messages")
    public ApiResponse<Map<String, Object>> messages(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long id,
            @RequestParam(required = false) Long beforeId,
            @RequestParam(required = false) Long afterId,
            @RequestParam(required = false) Integer size
    ) {
        return ApiResponse.ok(conversationService.messages(loginUser.userId(), id, beforeId, afterId, size));
    }

    @PostMapping("/api/im/conversations/{id}/messages")
    public ApiResponse<Map<String, Object>> send(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long id,
            @RequestBody SendMessageRequest request
    ) {
        return ApiResponse.ok(conversationService.send(
                loginUser.userId(), id, request.type(), request.content(), request.clientMsgId()));
    }

    @PostMapping("/api/im/conversations/{id}/read")
    public ApiResponse<Map<String, Object>> read(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long id,
            @RequestBody ReadConversationRequest request
    ) {
        if (request.lastReadMessageId() == null) {
            throw ApiException.validation(List.of(new FieldErrorItem("lastReadMessageId", "请传入已读消息")));
        }
        return ApiResponse.ok(conversationService.read(loginUser.userId(), id, request.lastReadMessageId()));
    }
}
