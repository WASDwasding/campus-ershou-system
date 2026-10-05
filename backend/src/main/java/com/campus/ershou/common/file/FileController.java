package com.campus.ershou.common.file;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ApiResponse;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.ErrorResponseWriter;
import com.campus.ershou.common.security.LoginUser;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class FileController {

    private final FileService fileService;
    private final FileUrls fileUrls;
    private final ErrorResponseWriter errorWriter;

    public FileController(FileService fileService, FileUrls fileUrls, ErrorResponseWriter errorWriter) {
        this.fileService = fileService;
        this.fileUrls = fileUrls;
        this.errorWriter = errorWriter;
    }

    @PostMapping(value = "/api/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> upload(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam("file") MultipartFile file,
            @RequestParam("purpose") String purpose
    ) {
        return ApiResponse.ok(fileService.upload(loginUser.userId(), file, purpose));
    }

    @GetMapping("/api/files/{fileId}/url")
    public ApiResponse<Map<String, Object>> url(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable String fileId
    ) {
        return ApiResponse.ok(fileService.urlFor(loginUser.userId(), fileId));
    }

    @GetMapping("/files/public/{fileId}")
    public void publicFile(
            @PathVariable String fileId,
            @RequestParam(required = false) String size,
            HttpServletResponse response
    ) throws IOException {
        writeImage(fileId, size, true, response);
    }

    @GetMapping("/api/files/private/{fileId}")
    public void privateFile(
            @PathVariable String fileId,
            @RequestParam(required = false) String size,
            @RequestParam long exp,
            @RequestParam String sig,
            HttpServletResponse response
    ) throws IOException {
        String normalized;
        try {
            normalized = fileUrls.normalizeSize(size);
        } catch (IllegalArgumentException ex) {
            errorWriter.write(response, ErrorCode.VALIDATION_FAILED, Map.of());
            return;
        }
        if (!fileUrls.validSignature(fileId, normalized, exp, sig)) {
            errorWriter.write(response, ErrorCode.FORBIDDEN, Map.of());
            return;
        }
        StoredFile stored = fileService.find(fileId);
        if (stored == null || stored.isPublic()) {
            errorWriter.write(response, ErrorCode.NOT_FOUND, Map.of());
            return;
        }
        writeImage(fileId, normalized, false, response);
    }

    private void writeImage(String fileId, String size, boolean mustBePublic, HttpServletResponse response) throws IOException {
        try {
            if (mustBePublic) {
                StoredFile stored = fileService.find(fileId);
                if (stored == null || !stored.isPublic()) {
                    throw ApiException.of(ErrorCode.NOT_FOUND);
                }
            }
            String normalized = size == null || size.isBlank() ? "" : size;
            if (!normalized.isEmpty() && !"thumb".equals(normalized)) {
                errorWriter.write(response, ErrorCode.VALIDATION_FAILED, Map.of());
                return;
            }
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            response.setHeader("Cache-Control", mustBePublic ? "public, max-age=86400" : "private, max-age=60");
            fileService.writeContent(fileId, normalized, response.getOutputStream());
        } catch (ApiException ex) {
            errorWriter.write(response, ex);
        }
    }
}
