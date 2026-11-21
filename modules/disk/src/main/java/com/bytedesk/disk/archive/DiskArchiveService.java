package com.bytedesk.disk.archive;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.criteria.Predicate;

@Service
@Slf4j
public class DiskArchiveService {
    private final DiskArchiveFileRepository files;
    private final DiskArchiveAccess access;
    private final DiskArchiveAudit audit;
    private final DiskArchiveStorage storage;
    // private final DiskArchiveSettings settings;
    private final TransactionTemplate transactions;

    public DiskArchiveService(DiskArchiveFileRepository files, DiskArchiveAccess access, DiskArchiveAudit audit,
            DiskArchiveStorage storage, DiskArchiveSettings settings, PlatformTransactionManager manager) {
        this.files = files;
        this.access = access;
        this.audit = audit;
        this.storage = storage;
        // this.settings = settings;
        this.transactions = new TransactionTemplate(manager);
    }

    public record Capabilities(boolean canRead, boolean canUpload, boolean canManage, long maxFileSize,
            boolean storageConfigured) {}

    public Capabilities capabilities(String module) {
        String normalized = DiskArchivePolicy.requiredModule(module);
        var actor = access.current();
        access.read(actor, normalized);
        return new Capabilities(true, actor.has(DiskArchivePermissions.upload(normalized)),
                actor.manager(normalized), storage.maxFileSize(), storage.configured());
    }

    public org.springframework.data.domain.Page<DiskArchiveResponse> query(DiskArchiveRequest request,
            boolean recycle) {
        String module = DiskArchivePolicy.requiredModule(request.getModule());
        var actor = access.current();
        access.read(actor, module);
        if (recycle && !actor.manager(module) && !actor.has(DiskArchivePermissions.delete(module))) {
            // Non-managers/non-deleters are not granted a full recycle view.
            throw new DiskArchiveException(403, "无权查看归档回收站");
        }
        return queryPage(request, module, actor, recycle, false);
    }

    private org.springframework.data.domain.Page<DiskArchiveResponse> queryPage(DiskArchiveRequest request,
            String module, DiskArchiveAccess.Actor actor, boolean recycle, boolean ownOnly) {
        String search = DiskArchivePolicy.optional(request.getSearch(), 255);
        SpecificationBuilder builder = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("orgUid"), actor.orgUid()));
            predicates.add(cb.equal(root.get("module"), module));
            predicates.add(cb.equal(root.get("deleted"), recycle));
            predicates.add(cb.equal(root.get("uploadState"), "READY"));
            if (ownOnly) predicates.add(cb.equal(root.get("userUid"), actor.userUid()));
            if (search != null) {
                String pattern = "%" + escapeLike(search.toLowerCase(Locale.ROOT)) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("originalName")), pattern, '\\'),
                        cb.like(cb.lower(root.get("contractNumber")), pattern, '\\'),
                        cb.like(cb.lower(root.get("counterparty")), pattern, '\\'),
                        cb.like(cb.lower(root.get("invoiceNumber")), pattern, '\\'),
                        cb.like(cb.lower(root.get("remark")), pattern, '\\')));
            }
            LocalDate start = request.getStartDate();
            LocalDate end = request.getEndDate();
            if (start != null || end != null) {
                jakarta.persistence.criteria.Path<LocalDate> dateColumn = "INVOICE".equals(module)
                        ? root.get("invoiceDate")
                        : root.get("signingDate");
                if (start != null) predicates.add(cb.or(cb.isNull(dateColumn),
                        cb.greaterThanOrEqualTo(dateColumn, start)));
                if (end != null) predicates.add(cb.or(cb.isNull(dateColumn),
                        cb.lessThanOrEqualTo(dateColumn, end)));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return files.findAll(builder, page(request)).map(f -> response(f, actor));
    }

    interface SpecificationBuilder extends org.springframework.data.jpa.domain.Specification<DiskArchiveFileEntity> {}

    public DiskArchiveResponse detail(String uid) {
        var actor = access.current();
        var file = require(uid, actor, false, null);
        access.read(actor, file.getModule());
        return response(file, actor);
    }

    public DiskArchiveResponse upload(MultipartFile content, DiskArchiveRequest request) {
        Reservation reservation = transactions.execute(status -> reserve(content, request));
        if (reservation == null) throw new IllegalStateException("Missing upload reservation");
        if (!reservation.write()) {
            return transactions.execute(status -> {
                var actor = access.current();
                var existing = require(reservation.uid(), actor, false, null);
                access.read(actor, existing.getModule());
                if (existing.isDeleted()) throw new DiskArchiveException(409, "此请求对应文件已移除，请使用新请求重传");
                return response(existing, actor);
            });
        }
        try {
            return transactions.execute(status -> publish(reservation, content));
        } catch (RuntimeException failure) {
            fail(reservation);
            throw failure;
        }
    }

    private record Reservation(String uid, String orgUid, boolean write) {}

    private Reservation reserve(MultipartFile content, DiskArchiveRequest request) {
        var actor = access.current();
        String module = DiskArchivePolicy.requiredModule(request.getModule());
        access.read(actor, module);
        actor.require(DiskArchivePermissions.upload(module));
        String requestId = DiskArchivePolicy.requestId(request.getUploadRequestId());
        var existing = files.findByOrgUidAndUserUidAndUploadRequestId(actor.orgUid(), actor.userUid(), requestId);
        if (existing.isPresent()) {
            if (existing.get().isDeleted()) {
                throw new DiskArchiveException(409, "此请求对应文件已移除，请使用新请求重传");
            }
            return new Reservation(existing.get().getUid(), actor.orgUid(), false);
        }
        if (content.isEmpty() || content.getSize() > storage.maxFileSize()) {
            throw new DiskArchiveException(413, "文件为空或超过大小限制");
        }
        String name = DiskArchivePolicy.required(content.getOriginalFilename(), 255);
        if (name.codePoints().anyMatch(c -> Character.isISOControl(c) || c == '/' || c == '\\')) {
            throw new DiskArchiveException(400, "文件名称不正确");
        }
        String type = storage.contentType(name);
        storage.root();
        applyMetadata(request, module, null);
        DiskArchiveFileEntity file = new DiskArchiveFileEntity();
        file.setUid(UUID.randomUUID().toString());
        file.setOrgUid(actor.orgUid());
        file.setUserUid(actor.userUid());
        file.setModule(module);
        file.setOriginalName(name);
        file.setContentType(type);
        file.setSize(content.getSize());
        file.setStorageBackend("PRIVATE_LOCAL");
        file.setStorageKey(UUID.randomUUID().toString());
        file.setUploadState("PENDING");
        file.setUploadRequestId(requestId);
        file.setRequestKey(DiskArchivePolicy.digest(actor.orgUid() + "\0" + actor.userUid() + "\0" + requestId));
        applyMetadata(request, module, file);
        files.saveAndFlush(file);
        return new Reservation(file.getUid(), actor.orgUid(), true);
    }

    private DiskArchiveResponse publish(Reservation reservation, MultipartFile content) {
        var actor = access.current();
        if (!actor.orgUid().equals(reservation.orgUid())) throw DiskArchiveException.unavailable();
        var file = files.lockByUidAndOrgUid(reservation.uid(), actor.orgUid())
                .orElseThrow(DiskArchiveException::unavailable);
        access.read(actor, file.getModule());
        actor.require(DiskArchivePermissions.upload(file.getModule()));
        if (!actor.userUid().equals(file.getUserUid()) || !"PENDING".equals(file.getUploadState())) {
            throw new DiskArchiveException(409, "文件状态已变化，请刷新后重试");
        }
        storage.store(file.getStorageKey(), content, file.getContentType());
        var refreshed = access.current();
        access.read(refreshed, file.getModule());
        refreshed.require(DiskArchivePermissions.upload(file.getModule()));
        file.setUploadState("READY");
        file.setUpdatedAt(ZonedDateTime.now());
        files.saveAndFlush(file);
        audit.success(actor, file.getUid(), "FILE_UPLOAD");
        return response(file, actor);
    }

    public DiskArchiveResponse update(DiskArchiveRequest request) {
        return transactions.execute(status -> {
            var actor = access.current();
            var file = require(request.getUid(), actor, true, Boolean.FALSE);
            access.write(actor, file.getModule(), file);
            version(file, request);
            applyMetadata(request, file.getModule(), file);
            if (request.getOriginalName() != null && !request.getOriginalName().isBlank()) {
                String name = DiskArchivePolicy.required(request.getOriginalName(), 255);
                if (name.codePoints().anyMatch(c -> Character.isISOControl(c) || c == '/' || c == '\\')) {
                    throw new DiskArchiveException(400, "文件名称不正确");
                }
                file.setOriginalName(name);
            }
            file.setUpdatedAt(ZonedDateTime.now());
            files.saveAndFlush(file);
            audit.success(actor, file.getUid(), "FILE_UPDATE");
            return response(file, actor);
        });
    }

    public DiskArchiveResponse delete(DiskArchiveRequest request) {
        return transactions.execute(status -> {
            var actor = access.current();
            var file = require(request.getUid(), actor, true, Boolean.FALSE);
            String module = file.getModule();
            boolean own = actor.userUid().equals(file.getUserUid());
            if (!actor.manager(module) && !(own && actor.has(DiskArchivePermissions.delete(module)))) {
                throw new DiskArchiveException(403, "无权删除该归档文件");
            }
            actor.require(DiskArchivePermissions.read(module));
            version(file, request);
            if (!"READY".equals(file.getUploadState())) throw DiskArchiveException.unavailable();
            file.setDeleted(true);
            file.setDeletedAt(ZonedDateTime.now());
            file.setDeletedBy(actor.userUid());
            file.setUpdatedAt(ZonedDateTime.now());
            files.saveAndFlush(file);
            audit.success(actor, file.getUid(), "FILE_DELETE");
            return response(file, actor);
        });
    }

    public DiskArchiveResponse restore(DiskArchiveRequest request) {
        return transactions.execute(status -> {
            var actor = access.current();
            var file = require(request.getUid(), actor, true, Boolean.TRUE);
            String module = file.getModule();
            boolean own = actor.userUid().equals(file.getUserUid());
            if (!actor.manager(module) && !(own && actor.has(DiskArchivePermissions.delete(module)))) {
                throw new DiskArchiveException(403, "无权恢复该归档文件");
            }
            actor.require(DiskArchivePermissions.read(module));
            version(file, request);
            file.setDeleted(false);
            file.setDeletedAt(null);
            file.setDeletedBy(null);
            file.setUpdatedAt(ZonedDateTime.now());
            files.saveAndFlush(file);
            audit.success(actor, file.getUid(), "FILE_RESTORE");
            return response(file, actor);
        });
    }

    private record Download(DiskArchiveAccess.Actor actor, DiskArchiveFileEntity file) {}

    public void download(String uid, boolean preview, HttpServletResponse response) throws IOException {
        Download download = transactions.execute(status -> {
            var actor = access.current();
            var file = require(uid, actor, false, Boolean.FALSE);
            access.read(actor, file.getModule());
            if (!"READY".equals(file.getUploadState())) throw DiskArchiveException.unavailable();
            if (preview && !List.of("application/pdf", "image/jpeg", "image/png").contains(file.getContentType())) {
                throw new DiskArchiveException(415, "此文件仅支持下载");
            }
            audit.record(actor, file.getUid(), preview ? "FILE_PREVIEW" : "FILE_DOWNLOAD", "AUTHORIZED", Map.of());
            return new Download(actor, file);
        });
        if (download == null) throw new IllegalStateException("Missing download authorization");
        var file = download.file();
        try (var input = storage.read(file.getStorageKey())) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, "private, no-store");
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("Content-Security-Policy", "sandbox; default-src 'none'");
            response.setContentType(file.getContentType());
            response.setContentLengthLong(file.getSize());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    (preview ? ContentDisposition.inline() : ContentDisposition.attachment())
                            .filename(file.getOriginalName(), java.nio.charset.StandardCharsets.UTF_8).build().toString());
            input.transferTo(response.getOutputStream());
        } catch (IOException | DiskArchiveException failure) {
            transactions.executeWithoutResult(status -> audit.record(download.actor(), file.getUid(),
                    preview ? "FILE_PREVIEW" : "FILE_DOWNLOAD", "FAILURE", Map.of()));
            throw failure;
        }
    }

    public DiskArchiveFileEntity require(String uid, DiskArchiveAccess.Actor actor, boolean lock, Boolean deleted) {
        if (uid == null || uid.isBlank()) throw DiskArchiveException.unavailable();
        var file = (lock ? files.lockByUidAndOrgUid(uid, actor.orgUid())
                : files.findByUidAndOrgUid(uid, actor.orgUid())).orElseThrow(DiskArchiveException::unavailable);
        if (deleted != null && file.isDeleted() != deleted) throw DiskArchiveException.unavailable();
        return file;
    }

    public static void version(DiskArchiveFileEntity file, DiskArchiveRequest request) {
        if (request.getVersion() == null || request.getVersion() != file.getVersion()) {
            throw new DiskArchiveException(409, "文件已更新，请刷新后重试");
        }
    }

    private void applyMetadata(DiskArchiveRequest request, String module, DiskArchiveFileEntity file) {
        if (file == null) {
            // Validation-only invocation for upload path.
            DiskArchivePolicy.optional(request.getContractNumber(), 200);
            DiskArchivePolicy.optional(request.getCounterparty(), 255);
            DiskArchivePolicy.optional(request.getInvoiceNumber(), 200);
            DiskArchivePolicy.optional(request.getRemark(), 2000);
            DiskArchivePolicy.amount(request.getAmount(), request.getCurrency());
            return;
        }
        file.setContractNumber(DiskArchivePolicy.optional(request.getContractNumber(), 200));
        file.setCounterparty(DiskArchivePolicy.optional(request.getCounterparty(), 255));
        file.setInvoiceNumber(DiskArchivePolicy.optional(request.getInvoiceNumber(), 200));
        file.setRemark(DiskArchivePolicy.optional(request.getRemark(), 2000));
        DiskArchivePolicy.amount(request.getAmount(), request.getCurrency());
        file.setAmount(request.getAmount());
        file.setCurrency(request.getAmount() == null ? null
                : (request.getCurrency() == null || request.getCurrency().isBlank() ? "CNY"
                        : request.getCurrency().strip().toUpperCase(Locale.ROOT)));
        if ("INVOICE".equals(module)) {
            file.setInvoiceDate(request.getInvoiceDate());
        } else {
            file.setSigningDate(request.getSigningDate());
        }
    }

    private DiskArchiveResponse response(DiskArchiveFileEntity f, DiskArchiveAccess.Actor actor) {
        var r = new DiskArchiveResponse();
        r.setUid(f.getUid());
        r.setVersion(f.getVersion());
        r.setModule(f.getModule());
        r.setOriginalName(f.getOriginalName());
        r.setContentType(f.getContentType());
        r.setSize(f.getSize());
        r.setContractNumber(f.getContractNumber());
        r.setCounterparty(f.getCounterparty());
        r.setInvoiceNumber(f.getInvoiceNumber());
        r.setAmount(f.getAmount() == null ? null : f.getAmount().toPlainString());
        r.setCurrency(f.getCurrency());
        r.setSigningDate(f.getSigningDate());
        r.setInvoiceDate(f.getInvoiceDate());
        r.setRemark(f.getRemark());
        r.setDeleted(f.isDeleted());
        r.setUpdatedAt(f.getUpdatedAt());
        r.setCreatedAt(f.getCreatedAt());
        r.setUploadState(f.getUploadState());
        List<String> actions = new ArrayList<>();
        if ("READY".equals(f.getUploadState())) {
            actions.add("DOWNLOAD");
            boolean own = actor.userUid().equals(f.getUserUid());
            if (!f.isDeleted() && (own || actor.manager(f.getModule()))) actions.add("UPDATE");
            if (own || actor.manager(f.getModule())) {
                actions.add(f.isDeleted() ? "RESTORE" : "DELETE");
            }
        }
        r.setActions(actions);
        return r;
    }

    private static Pageable page(DiskArchiveRequest request) {
        if (request.getPageNumber() < 0 || request.getPageNumber() > 100000 || request.getPageSize() < 1
                || request.getPageSize() > 100) throw new DiskArchiveException(400, "分页参数不正确");
        return PageRequest.of(request.getPageNumber(), request.getPageSize(),
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.asc("uid")));
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private void fail(Reservation reservation) {
        try {
            transactions.executeWithoutResult(status -> cleanup(reservation.uid(), reservation.orgUid()));
        } catch (RuntimeException cleanupFailure) {
            log.error("Archive upload cleanup failed; reconciliation required, fileUid={}, type={}",
                    reservation.uid(), cleanupFailure.getClass().getSimpleName());
        }
    }

    private void cleanup(String uid, String orgUid) {
        var file = files.lockByUidAndOrgUid(uid, orgUid).orElseThrow(DiskArchiveException::unavailable);
        if ("READY".equals(file.getUploadState()) || file.isStorageCleared()) return;
        file.setUploadState("FAILED");
        storage.deleteUnpublished(file.getStorageKey());
        file.setStorageCleared(true);
        files.saveAndFlush(file);
        audit.record(new DiskArchiveAccess.Actor(file.getUserUid(), orgUid, Set.of()),
                file.getUid(), "FILE_UPLOAD", "FAILURE", Map.of());
    }

    @Scheduled(fixedDelay = 3600000, initialDelay = 3600000)
    public void reconcile() {
        var expired = files.findByStorageClearedFalseAndUploadStateInAndCreatedAtBefore(
                List.of("PENDING", "FAILED"), ZonedDateTime.now().minusHours(24), PageRequest.of(0, 100));
        for (var file : expired) {
            fail(new Reservation(file.getUid(), file.getOrgUid(), false));
        }
    }
}
