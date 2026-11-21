package com.bytedesk.disk.archive;

import java.io.IOException;
import com.bytedesk.core.utils.JsonResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@Tag(name = "网盘归档文件库")
@RestController
@RequestMapping("/api/v1/disk/archive")
@RequiredArgsConstructor
public class DiskArchiveRestController {
    private final DiskArchiveService archives;

    @ModelAttribute
    public void noStore(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
    }

    @GetMapping("/capabilities")
    public ResponseEntity<?> capabilities(@org.springframework.web.bind.annotation.RequestParam String module) {
        return ok(archives.capabilities(module));
    }

    @GetMapping("/query")
    public ResponseEntity<?> query(DiskArchiveRequest request) { return ok(archives.query(request, false)); }

    @GetMapping("/query/recycle")
    public ResponseEntity<?> recycle(DiskArchiveRequest request) { return ok(archives.query(request, true)); }

    @GetMapping("/query/uid")
    public ResponseEntity<?> detail(@RequestParam String uid) { return ok(archives.detail(uid)); }

    @PostMapping("/create")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile content, DiskArchiveRequest request) {
        return ok(archives.upload(content, request));
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody DiskArchiveRequest request) { return ok(archives.update(request)); }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DiskArchiveRequest request) { return ok(archives.delete(request)); }

    @PostMapping("/restore")
    public ResponseEntity<?> restore(@RequestBody DiskArchiveRequest request) { return ok(archives.restore(request)); }

    @GetMapping("/file/preview")
    public void preview(@RequestParam String uid, HttpServletResponse response) throws IOException {
        archives.download(uid, true, response);
    }

    @GetMapping("/file/download")
    public void download(@RequestParam String uid, HttpServletResponse response) throws IOException {
        archives.download(uid, false, response);
    }

    @ExceptionHandler(DiskArchiveException.class)
    public ResponseEntity<?> archiveError(DiskArchiveException failure) {
        log.warn("Archive request rejected, status={}", failure.getStatus());
        return ResponseEntity.status(failure.getStatus())
                .body(JsonResult.error(failure.getMessage(), failure.getStatus()));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, OptimisticLockingFailureException.class,
            PessimisticLockingFailureException.class})
    public ResponseEntity<?> conflict(RuntimeException failure) {
        log.warn("Archive write conflict, type={}", failure.getClass().getSimpleName());
        return ResponseEntity.status(409).body(JsonResult.error("记录已变化，请刷新后重试", 409));
    }

    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.validation.BindException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ResponseEntity<?> invalid(Exception failure) {
        log.warn("Invalid archive request, type={}", failure.getClass().getSimpleName());
        return ResponseEntity.badRequest().body(JsonResult.error("归档请求参数不正确", 400));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> tooLarge() {
        return ResponseEntity.status(413).body(JsonResult.error("文件超过服务器上传限制", 413));
    }

    private ResponseEntity<?> ok(Object data) {
        return ResponseEntity.ok(JsonResult.success(data));
    }
}
