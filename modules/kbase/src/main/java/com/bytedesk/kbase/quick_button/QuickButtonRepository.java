/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Repository for quick buttons
 */
package com.bytedesk.kbase.quick_button;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuickButtonRepository extends JpaRepository<QuickButtonEntity, Long>, JpaSpecificationExecutor<QuickButtonEntity> {

    Optional<QuickButtonEntity> findByUid(String uid);
}
