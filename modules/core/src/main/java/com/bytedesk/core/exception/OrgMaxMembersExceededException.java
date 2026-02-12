package com.bytedesk.core.exception;

import lombok.Getter;

@Getter
public class OrgMaxMembersExceededException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String orgUid;
    private final Integer maxMembers;
    private final Long currentDistinctUsers;

    public OrgMaxMembersExceededException(String orgUid, Integer maxMembers, Long currentDistinctUsers, String message) {
        super(message);
        this.orgUid = orgUid;
        this.maxMembers = maxMembers;
        this.currentDistinctUsers = currentDistinctUsers;
    }
}
