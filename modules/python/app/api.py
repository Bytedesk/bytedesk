'''
Author: jackning 270580156@qq.com
Date: 2024-08-29 10:35:52
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-08-29 10:38:28
Description: bytedesk.com https://github.com/Bytedesk/bytedesk
  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 contact: 270580156@qq.com 
 技术/商务联系：270580156@qq.com
Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
'''
# 
from fastapi import APIRouter

from app import chat, config, doc, tts
#
api_v1_router = APIRouter()
api_v1_router.include_router(chat.router)
api_v1_router.include_router(config.router)
api_v1_router.include_router(doc.router)
api_v1_router.include_router(tts.router)
