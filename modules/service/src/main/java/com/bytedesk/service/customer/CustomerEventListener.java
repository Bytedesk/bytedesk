/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 07:21:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-18 07:21:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdUploadUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

	private final UploadRestService uploadRestService;

	private final CustomerRepository customerRepository;

	private final UidUtils uidUtils;

	@EventListener
	public void onUploadCreateEvent(UploadCreateEvent event) {
		UploadEntity upload = event.getUpload();
		if (upload == null) {
			return;
		}

		if (!UploadTypeEnum.CUSTOMER.name().equalsIgnoreCase(upload.getType())) {
			return;
		}

		String fileName = upload.getFileName();
		if (!BdUploadUtils.isExcelFile(fileName)) {
			log.warn("不是Excel文件，无法导入客户: {}", fileName);
			return;
		}

		try {
			Resource resource = uploadRestService.loadAsResource(upload);
			if (!resource.exists()) {
				log.warn("客户导入文件不存在: {}", fileName);
				return;
			}
			String filePath = resource.getFile().getAbsolutePath();
			log.info("CustomerEventListener CUSTOMER import: {}", filePath);

			// 使用 headMap 映射，兼容表头别名（如“手机/手机号码”）和导出文件包含 uid 列的情况
			EasyExcel.read(filePath)
					.headRowNumber(1)
					.registerReadListener(new CustomerExcelListener(customerRepository, uidUtils, upload.getOrgUid()))
					.sheet()
					.doRead();
		} catch (Exception e) {
			log.error("CustomerEventListener 客户导入失败: {}", e.getMessage(), e);
		}
	}
}
