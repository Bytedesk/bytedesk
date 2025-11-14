/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-11-14
 * @Description: Email Template Repository
 */
package com.bytedesk.core.email_template;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplateEntity, Long>, JpaSpecificationExecutor<EmailTemplateEntity> {

    Optional<EmailTemplateEntity> findByUid(String uid);

    Boolean existsByUid(String uid);
}
