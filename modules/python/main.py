'''
Author: jackning 270580156@qq.com
Date: 2024-08-29 09:40:39
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-08-31 00:04:42
Description: bytedesk.com https://github.com/Bytedesk/bytedesk
  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 contact: 270580156@qq.com 
 技术/商务联系：270580156@qq.com
Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
'''
from contextlib import asynccontextmanager
import logging
from fastapi import FastAPI
import uvicorn

from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

from app.api import api_v1_router
from app.config import get_settings
from app.redis import defaultSubscribe
# from app.doc import load_and_parse

# 配置日志
logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
logger = logging.getLogger(__name__)
# 
app = FastAPI()

# https://fastapi.tiangolo.com/advanced/events/
@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Bytedesk AI Starting server...")
    defaultSubscribe()
    # load_and_parse(fileUid='df_file_uid', filePath='bytedesk.md', kbUid='df_kb_uid')
    yield

# https://fastapi.tiangolo.com/zh/tutorial/bigger-applications/
app = FastAPI(
    title='ai.bytedesk.com',
    lifespan=lifespan,
    # docs_url=None, 
    # redoc_url=None, 
    # openapi_url=None
)
# 
app.add_middleware(
    CORSMiddleware,
    allow_credentials=True,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)
app.include_router(api_v1_router, prefix=get_settings().API_V1_PREFIX)
# 
# https://fastapi.tiangolo.com/tutorial/static-files/
# http://127.0.0.1:9007/
app.mount("/", StaticFiles(directory="static", html=True), name="static")

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=9007,
                log_level="info", reload=True)
