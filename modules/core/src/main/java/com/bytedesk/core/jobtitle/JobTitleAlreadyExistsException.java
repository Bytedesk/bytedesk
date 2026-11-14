/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.jobtitle;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.BusinessConflictException;

/**
 * 同组织同名有效职务已存在（含并发创建竞争）。
 */
public class JobTitleAlreadyExistsException extends BusinessConflictException {

    private static final long serialVersionUID = 1L;

    public JobTitleAlreadyExistsException(String name) {
        super(I18Consts.withArgs(I18Consts.I18N_DATA_ALREADY_EXISTS, name));
    }
}
