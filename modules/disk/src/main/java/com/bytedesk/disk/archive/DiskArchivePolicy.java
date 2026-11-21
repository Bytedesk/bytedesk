package com.bytedesk.disk.archive;

public final class DiskArchivePolicy {
    public static final java.util.Set<String> MODULES = java.util.Set.of("CONTRACT", "INVOICE");

    private DiskArchivePolicy() {}

    public static String requiredModule(String module) {
        if (module == null || !MODULES.contains(module)) {
            throw new DiskArchiveException(400, "归档模块不正确");
        }
        return module;
    }

    public static String required(String value, int length) {
        if (value == null || value.isBlank() || value.strip().length() > length) {
            throw new DiskArchiveException(400, "必填字段为空或长度超限");
        }
        return value.strip();
    }

    public static String optional(String value, int length) {
        if (value == null || value.isBlank()) return null;
        if (value.strip().length() > length) throw new DiskArchiveException(400, "字段长度超限");
        return value.strip();
    }

    public static void amount(java.math.BigDecimal amount, String currency) {
        if (amount == null) return;
        if (amount.signum() < 0 || amount.scale() > 2
                || amount.precision() - amount.scale() > 17) {
            throw new DiskArchiveException(400, "金额需为非负数，最多两位小数");
        }
        String code = currency == null || currency.isBlank() ? "CNY"
                : currency.strip().toUpperCase(java.util.Locale.ROOT);
        try {
            java.util.Currency.getInstance(code);
        } catch (IllegalArgumentException e) {
            throw new DiskArchiveException(400, "币种不正确");
        }
    }

    public static String requestId(String requestId) {
        String id = required(requestId, 64);
        if (!id.matches("[A-Za-z0-9._-]{1,64}")) throw new DiskArchiveException(400, "上传请求编号不正确");
        return id;
    }

    public static String digest(String value) {
        try {
            return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
