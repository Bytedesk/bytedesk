package com.bytedesk.core.group;

import java.util.List;

/**
 * SPI for validating robot uids when adding robots to a group.
 * Implemented by the ai module (which owns RobotEntity) to avoid a core -> ai compile dependency.
 * 
 * Fail-closed contract: when a group carries non-empty robotUids, the ai-side implementation
 * must be present and must reject unknown/invalid uids. When the ai module is not assembled
 * (core-only deployments), GroupRestService refuses robot management instead of silently
 * accepting unvalidated uids.
 */
public interface GroupRobotValidator {

    /**
     * Validate that all robotUids exist, are not deleted, belong to the given orgUid
     * (system robots allowed via org sharing rules), and are usable by the operator.
     *
     * @param robotUids robot uids to validate (already trimmed/deduped by caller)
     * @param orgUid    organization uid of the group
     * @param userUid   operator user uid for per-user authorization checks
     * @return list of invalid robot uids (empty means all valid)
     */
    List<String> findInvalidRobotUids(List<String> robotUids, String orgUid, String userUid);
}
