'''
Author: jackning 270580156@qq.com
Date: 2024-08-29 09:55:35
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-08-31 11:40:51
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
# import time
import logging
from typing import List
import redis
import json
import uuid
from app.redisPubsub import defaultPublish, publishDeleteFileError, publishDeleteFileSuccess, publishParseFileSuccess
from app.redisVector import myredisVector
from app.config import get_settings
from app.doc import load_and_parse
from app.utils import download_file
from app.consts import DELETE_FILE, PARSE_FILE, QUESTION, pubsubChannel

redisClient = redis.Redis(host=get_settings().REDIS_HOST,
                          password=get_settings().REDIS_PASSWORD,
                          port=get_settings().REDIS_PORT,
                          decode_responses=True)


def defaultSubscribe() -> None:
    subscribe(pubsubChannel)
    # subscribe(pubsubObjectChannel)

def subscribe(channel):
    logging.info(f'subscribe channel: {channel}')
    pubsub = redisClient.pubsub()
    pubsub.subscribe(channel)
    pubsub.subscribe(**{channel: on_message})
    pubsub.run_in_thread(0.1)
# 
def on_message(message):
    # {"content":"http://127.0.0.1:9003/file/240828150817_北京软考通知.pdf","type":"UPLOAD_FILE"}
    # logging.info(f"on_message Received message: {message['data']}")
    # 解析消息字符串为字典
    data_dict = json.loads(message['data'])
    # 获取content和type的内容
    type_ = data_dict.get('type')
    content = data_dict.get('content')
    content_dict = json.loads(content)
    if type_ == PARSE_FILE:
        fileUid = content_dict.get('fileUid')
        fileUrl = content_dict.get('fileUrl')
        kbUid = content_dict.get('kbUid')
        # 打印获取到的content和type
        logging.info(f"Received type: {type_}, {fileUid}, {fileUrl}, {kbUid}")
        parse_file(fileUid, fileUrl, kbUid)
    elif type_ == DELETE_FILE:
        fileUid = content_dict.get('fileUid')
        docIds = content_dict.get('docIds')
        logging.info(f"Received type: {type_}, {fileUid}, {docIds}")
        delete_docs(fileUid=fileUid, docIds=docIds)
    elif type_ == QUESTION:
        uid = content_dict.get('uid')
        threadTopic = content_dict.get('threadTopic')
        kbUid = content_dict.get('kbUid')
        question = content_dict.get('question')
        # 打印获取到的content和type
        logging.info(f"Received type: {type_}, {threadTopic}, {kbUid}, {question}")
        search_results = myredisVector.search_docs(kbUid=kbUid, query=question)
        myredisVector.query_llm(messageUid=uid, threadTopic=threadTopic, kbUid=kbUid, question=question, search_results=search_results)
    # else:
        # logging.info(f"Received unknown type: {type_}")


def parse_file(fileUid: str, fileUrl: str, kbUid: str):
    # 下载文件到file文件夹
    # 如果fileUrl存在，则下载文件
    if fileUrl:
        filePath = download_file(fileUrl)
        logging.info(f"Saved file: {filePath}")
    # 解析
    docIds = load_and_parse(fileUid=fileUid, filePath=filePath, kbUid=kbUid)
    #
    publishParseFileSuccess(fileUid=fileUid, docIds=docIds)
    return

def delete_docs(fileUid: str, docIds: List[str]):
    # logging.info(f"delete fileUid: {fileUid}, {docIds}")
    result = myredisVector.delete_docs(docIds=docIds)
    #
    if result:
        logging.info(f"delete fileUid success: {result} {docIds}")
        publishDeleteFileSuccess(fileUid=fileUid)
    else:
        logging.info(f"delete fileUid fail: {result} {docIds}")
        publishDeleteFileError(fileUid=fileUid, errorMsg="delete fail")
    return


def setKey(key: str, value: str):
    redisClient.set(key, value)


def getKey(key: str):
    redisClient.get(key)
