'''
Author: jackning 270580156@qq.com
Date: 2023-12-26 11:20:33
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-08-31 19:18:09
Description: bytedesk.com https://github.com/Bytedesk/bytedesk
  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 contact: 270580156@qq.com 
 技术/商务联系：270580156@qq.com
Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
'''
from functools import lru_cache
from typing import Annotated
from fastapi import APIRouter, Depends
from pydantic_settings import BaseSettings

# https://fastapi.tiangolo.com/zh/advanced/settings/#pydantic-settings 
class Settings(BaseSettings):
    DEBUG: bool
    API_V1_PREFIX: str 
    # 
    EMBEDDINGS_PATH: str
    #
    ZHIPU_API_KEY: str
    # 连接MySQL数据库
    DATABASE_URL: str
    ASYNC_DATABASE_URL: str
    # REDIS
    REDIS_HOST: str
    REDIS_PORT: int
    REDIS_PASSWORD: str
    REDIS_URL: str
    REDIS_KEY_PREFIX: str
    REDIS_INDEX_NAME: str
    IS_VECTOR_STORE_INITIATED: str

    # https://docs.pydantic.dev/latest/api/config/
    # https://fastapi.tiangolo.com/zh/advanced/settings/#env_1
    class Config:
        case_sensitive = False
        # 配置环境变量文件
        env_file = ".env"
# 
# settings = Settings()

@lru_cache(maxsize=32)
def get_settings():
    return Settings()

router = APIRouter(
    prefix='/settings',
    tags=['settings v1 apis']
)

# # http://127.0.0.1:9007/api/v1/settings/info
# @router.get("/info")
# async def info(settings: Annotated[Settings, Depends(get_settings)]):
#     print('cache info: ', get_settings.cache_info(), settings.API_V1_PREFIX)
#     if (settings.DEBUG):
#         return {
#             "env": settings.model_dump()
#         }
#     return {
#         "env": 'None'
#     }

# # http://127.0.0.1:9007/api/v1/settings/clear_cache
# # 添加一个清除缓存的路由
# @router.get("/clear_cache")
# def clear_cache():
#     print('cache info: ', get_settings.cache_info())
#     get_settings.cache_clear()  # 清除get_settings函数的缓存
#     return {"message": "Cache cleared"}
