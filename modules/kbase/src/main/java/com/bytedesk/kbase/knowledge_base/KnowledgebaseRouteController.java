/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-02 12:34:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 10:42:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kbase")
public class KnowledgebaseRouteController {

    // http://127.0.0.1:9003/kbase/
    @GetMapping({"", "/"})
    public String index() {
        return "kbase/index";
    }
    
}
