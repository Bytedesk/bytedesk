'''
Author: jackning 270580156@qq.com
Date: 2024-08-29 09:55:35
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-08-31 19:19:57
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
import logging
from fastapi import APIRouter, Request
from app.redisVector import myredisVector

router = APIRouter(
    prefix='/chat',
    tags=['chat v1 apis']
)

# http://127.0.0.1:9007/api/v1/chat/query?kbuid=1461090177253570&query=报名条件
# http://127.0.0.1:9007/api/v1/chat/query?kbuid=1461487033909519&query=DataStructure
@router.get("/query")
def query(kbuid: str, query: str):
    # 测试搜索结果
    search_results = myredisVector.search_docs(kbUid=kbuid, query=query)
    # search_results = myredisVector.search_as_retriever(kbUid=kbuid, query=query)
    return {
        "results": search_results
    }

# # http://127.0.0.1:9007/api/v1/chat/stream?kbuid=1461090177253570&query=报名条件
# # http://127.0.0.1:9007/api/v1/chat/stream?kbuid=1461487033909519&query=DataStructure
# @router.get("/stream")
# async def query(kbuid: str, query: str):
#     logging.info(f'stream: {kbuid}, {query}')
#     # TODO: query from db/cache, if match then return, if not then goto llm
#     search_results = myredisVector.search_docs(kbUid=kbuid, query=query)
#     logging.info(f'搜索结果: count={ len(search_results) }')
#     await myredisVector.query_llm(messageUid='', threadTopic='', kbUid=kbuid, question=query, search_results=search_results)
#     return {
#         'message': 'ok'
#     }
