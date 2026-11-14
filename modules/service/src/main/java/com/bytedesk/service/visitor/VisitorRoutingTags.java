package com.bytedesk.service.visitor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class VisitorRoutingTags {

    private static final int MAX_TAGS = 10;
    private static final int MAX_INPUT_LENGTH = 1024;
    private static final Pattern UID = Pattern.compile("[A-Za-z0-9_-]{1,64}");

    private VisitorRoutingTags() {
    }

    public static List<String> normalize(List<?> input) {
        if (input == null) {
            return new ArrayList<>();
        }
        if (input.size() > MAX_INPUT_LENGTH) {
            throw new IllegalArgumentException("routingTags input exceeds 1024 characters");
        }
        int inputLength = Math.max(input.size() - 1, 0);
        Set<String> tags = new LinkedHashSet<>();
        for (Object item : input) {
            if (!(item instanceof String value)) {
                throw new IllegalArgumentException("routingTags must contain only UID strings");
            }
            inputLength += value.length();
            if (inputLength > MAX_INPUT_LENGTH) {
                throw new IllegalArgumentException("routingTags input exceeds 1024 characters");
            }
            String uid = value.trim();
            if (uid.isEmpty()) {
                continue;
            }
            if (!UID.matcher(uid).matches()) {
                throw new IllegalArgumentException("routingTags UID must use 1-64 letters, digits, '_' or '-'");
            }
            tags.add(uid);
            if (tags.size() > MAX_TAGS) {
                throw new IllegalArgumentException("routingTags must contain at most 10 distinct UIDs");
            }
        }
        return new ArrayList<>(tags);
    }
}
