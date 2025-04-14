package com.bytedesk.ai.robot_message;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RobotMessageRepository extends JpaRepository<RobotMessageEntity, String>, JpaSpecificationExecutor<RobotMessageEntity> {
    
    Optional<RobotMessageEntity> findByUid(String uid);

    Boolean existsByUid(String uid);
}
