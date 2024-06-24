package com.bytedesk.core.category;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class CategoryEventListener {

    private final CategoryService categoryService;

    @Order(4)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        Organization organization = (Organization) event.getSource();
        String orgUid = organization.getUid();
        log.info("faq - organization created: {}", organization.getName());
        //
        CategoryRequest categoryFaqDemoRequest1 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_1)
                .orderNo(0)
                .level(LevelEnum.ORGNIZATION)
                .platform(BdConstants.PLATFORM_BYTEDESK)
                // .orgUid(orgUid)
                .build();
        categoryFaqDemoRequest1.setType(CategoryConsts.CATEGORY_TYPE_FAQ);
        categoryFaqDemoRequest1.setUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_1);
        categoryFaqDemoRequest1.setOrgUid(orgUid);
        categoryService.create(categoryFaqDemoRequest1);
        //
        CategoryRequest categoryFaqDemoRequest2 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_2)
                .orderNo(0)
                .level(LevelEnum.ORGNIZATION)
                .platform(BdConstants.PLATFORM_BYTEDESK)
                // .orgUid(orgUid)
                .build();
        categoryFaqDemoRequest2.setType(CategoryConsts.CATEGORY_TYPE_FAQ);
        categoryFaqDemoRequest2.setUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_2);
        categoryFaqDemoRequest1.setOrgUid(orgUid);
        categoryService.create(categoryFaqDemoRequest2);

    }
}
