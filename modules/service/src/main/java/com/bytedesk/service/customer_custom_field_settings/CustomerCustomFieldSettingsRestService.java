package com.bytedesk.service.customer_custom_field_settings;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.model.CustomFieldItem;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerCustomFieldSettingsRestService {

    private final CustomerCustomFieldSettingsRepository repository;

    private final UidUtils uidUtils;

    @Transactional(readOnly = true)
    public List<CustomFieldItem> queryByOrg(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            return List.of();
        }
        return repository.findByOrgUidAndDeleted(orgUid, false)
                .map(CustomerCustomFieldSettingsEntity::getCustomFieldList)
                .orElse(List.of());
    }

    @Transactional
    public List<CustomFieldItem> updateByOrg(String orgUid, List<CustomFieldItem> customFieldList) {
        if (!StringUtils.hasText(orgUid)) {
            throw new IllegalArgumentException("orgUid is required");
        }

        Optional<CustomerCustomFieldSettingsEntity> optional = repository.findByOrgUidAndDeleted(orgUid, false);

        CustomerCustomFieldSettingsEntity entity = optional.orElseGet(() -> CustomerCustomFieldSettingsEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(orgUid)
                .deleted(false)
                .build());

        entity.setCustomFieldList(customFieldList == null ? List.of() : customFieldList);
        repository.save(entity);
        return entity.getCustomFieldList();
    }
}
