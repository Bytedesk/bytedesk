/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:41:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-25 15:52:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    private final ModelMapper modelMapper;
  
    private final UidUtils uidUtils;

    public Action create(ActionRequest actionRequest) {

        Action action = modelMapper.map(actionRequest, Action.class);
        action.setAid(uidUtils.getCacheSerialUid());

        return save(action);
    }

    public Action save(Action action) {
        return actionRepository.save(action);
    }

    public ActionResponse convertToActionResponse(Action action) {
        return modelMapper.map(action, ActionResponse.class);
    }
    
}
