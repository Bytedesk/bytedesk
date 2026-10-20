package com.bytedesk.core.rbac.user;

/**
 * Maps an organization-scoped member or agent UID to its account UID.
 */
public interface UserIdentityRow {

    String getUid();

    String getUserUid();
}
