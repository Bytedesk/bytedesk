package com.bytedesk.disk.file;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 网盘文件/空间 Controller
 */
@Tag(name = "网盘文件管理", description = "企业网盘文件与空间 API")
@RestController
@RequestMapping("/api/v1/disk")
@RequiredArgsConstructor
public class DiskFileRestController {

    private final DiskFileRestService diskFileRestService;

    @Operation(summary = "查询我的空间", description = "个人空间（懒创建）+ 组织共享空间")
    @GetMapping("/space/query")
    public ResponseEntity<?> queryMySpaces() {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.queryMySpaces()));
    }

    @Operation(summary = "目录分页列表", description = "文件夹在前，含子节点计数；fileType 支持分类编码（IMAGE/VIDEO/AUDIO/PDF/DOC/SHEET/SLIDE/ARCHIVE）或扩展名")
    @GetMapping("/file/query/folder")
    public ResponseEntity<?> queryByFolder(DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.queryByFolder(request)));
    }

    @Operation(summary = "目录树（懒加载子目录）")
    @GetMapping("/file/query/tree")
    public ResponseEntity<?> queryFolderTree(DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.queryFolderTree(request)));
    }

    @Operation(summary = "内链定位", description = "鉴权后返回节点信息")
    @GetMapping("/file/query/uid")
    public ResponseEntity<?> queryByUidForNavigate(DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.queryByUidForNavigate(request)));
    }

    @Operation(summary = "面包屑", description = "从节点向上到空间根")
    @GetMapping("/file/query/breadcrumb")
    public ResponseEntity<?> queryBreadcrumb(DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.queryBreadcrumb(request)));
    }

    @Operation(summary = "文件名搜索", description = "当前空间，逐条权限过滤；fileType 支持分类编码或扩展名")
    @GetMapping("/file/search")
    public ResponseEntity<?> search(DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.search(request)));
    }

    @Operation(summary = "新建文件夹")
    @PostMapping("/file/create")
    public ResponseEntity<?> createFolder(@RequestBody DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.createFolder(request)));
    }

    @Operation(summary = "上传文件", description = "multipart 上传，复用 core 存储与安全校验")
    @PostMapping("/file/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.upload(file, request)));
    }

    @Operation(summary = "重命名")
    @PostMapping("/file/rename")
    public ResponseEntity<?> rename(@RequestBody DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.rename(request)));
    }

    @Operation(summary = "移动（批量，防环）")
    @PostMapping("/file/move")
    public ResponseEntity<?> move(@RequestBody DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.move(request)));
    }

    @Operation(summary = "复制（批量，递归目录）")
    @PostMapping("/file/copy")
    public ResponseEntity<?> copy(@RequestBody DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.copy(request)));
    }

    @Operation(summary = "软删除（批量，入回收站）")
    @PostMapping("/file/delete")
    public ResponseEntity<?> delete(@RequestBody DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.deleteBatch(request)));
    }

    @Operation(summary = "回收站分页", description = "支持 fileType 分类编码或扩展名过滤")
    @GetMapping("/file/query/recycle")
    public ResponseEntity<?> queryRecycle(DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.queryRecycle(request)));
    }

    @Operation(summary = "恢复（批量）")
    @PostMapping("/file/restore")
    public ResponseEntity<?> restore(@RequestBody DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.restoreBatch(request)));
    }

    @Operation(summary = "彻底删除（批量）")
    @PostMapping("/file/delete/permanent")
    public ResponseEntity<?> deletePermanent(@RequestBody DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.deletePermanent(request)));
    }

    @Operation(summary = "清空回收站")
    @PostMapping("/file/recycle/clear")
    public ResponseEntity<?> clearRecycle(@RequestBody DiskFileRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskFileRestService.clearRecycle(request)));
    }

    @Operation(summary = "下载（流式）")
    @GetMapping("/file/download")
    public void download(DiskFileRequest request, HttpServletResponse response) {
        diskFileRestService.download(request, response);
    }

    @Operation(summary = "受控预览")
    @GetMapping("/file/preview")
    public void preview(DiskFileRequest request, HttpServletResponse response) {
        diskFileRestService.preview(request, response);
    }

    @Operation(summary = "ZIP 批量下载", description = "先整体鉴权与条目数检查，再流式打包")
    @PostMapping("/file/download/zip")
    public void downloadZip(@RequestBody DiskFileRequest request, HttpServletResponse response) {
        diskFileRestService.downloadZip(request, response);
    }
}
