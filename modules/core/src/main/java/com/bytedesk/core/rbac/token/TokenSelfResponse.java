package com.bytedesk.core.rbac.token;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 本人登录会话脱敏响应：不返回 accessToken/refreshToken/完整 description 等凭证字段。
 * 时间统一输出带时区的 ISO-8601，客户端负责本地化展示。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenSelfResponse {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private String uid;

    private String name;

    private String device;

    private String channel;

    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime expiresAt;

    private Boolean permanent;

    private Boolean revoked;

    private String revokeReason;

    private Boolean isCurrent;

    /** VALID/EXPIRED/REVOKED/INVALID，服务端判断 */
    private String status;

    public String getCreatedAtIso() {
        return createdAt == null ? null : ISO_FORMATTER.format(createdAt);
    }

    public String getExpiresAtIso() {
        return expiresAt == null ? null : ISO_FORMATTER.format(expiresAt);
    }

    public static TokenSelfStatusEnum statusOf(TokenEntity entity, ZonedDateTime now) {
        if (Boolean.TRUE.equals(entity.getRevoked())) {
            return TokenSelfStatusEnum.REVOKED;
        }
        if (Boolean.TRUE.equals(entity.getPermanent())) {
            return TokenSelfStatusEnum.VALID;
        }
        if (entity.getExpiresAt() == null) {
            return TokenSelfStatusEnum.INVALID;
        }
        return entity.getExpiresAt().isAfter(now)
                ? TokenSelfStatusEnum.VALID
                : TokenSelfStatusEnum.EXPIRED;
    }

    public static TokenSelfResponse fromEntity(TokenEntity entity, boolean isCurrent, ZonedDateTime now) {
        return TokenSelfResponse.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .device(entity.getDevice())
                .channel(entity.getChannel())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .permanent(entity.getPermanent())
                .revoked(entity.getRevoked())
                .revokeReason(entity.getRevokeReason())
                .isCurrent(isCurrent)
                .status(statusOf(entity, now).name())
                .build();
    }
}
