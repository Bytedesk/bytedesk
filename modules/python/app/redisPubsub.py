'''
Author: jackning 270580156@qq.com
Date: 2024-08-29 18:21:14
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-08-31 10:18:58
Description: bytedesk.com https://github.com/Bytedesk/bytedesk
  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 contact: 270580156@qq.com 
 技术/商务联系：270580156@qq.com
Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
'''
import json
import logging
from typing import List
import redis
from app.config import get_settings
from app.consts import DELETE_FILE_ERROR, DELETE_FILE_SUCCESS, PARSE_FILE_ERROR, PARSE_FILE_SUCCESS, ANSWER, ANSWER_FINISHED, pubsubChannel
#
redisClient = redis.Redis(host=get_settings().REDIS_HOST,
                          password=get_settings().REDIS_PASSWORD,
                          port=get_settings().REDIS_PORT,
                          decode_responses=True)
#


def publishParseFileSuccess(fileUid: str, docIds: List[str]) -> None:
    content = json.dumps({
        "fileUid": fileUid,
        "docIds": docIds
    })
    message = json.dumps({
        "type": PARSE_FILE_SUCCESS,
        "content": content,
    }, ensure_ascii=False)
    defaultPublish(content=message)


def publishParseFileError(fileUid: str, errorMsg: str) -> None:
    content = json.dumps({
        "fileUid": fileUid,
        "errorMsg": errorMsg,
    })
    message = json.dumps({
        "type": PARSE_FILE_ERROR,
        "content": content,
    }, ensure_ascii=False)
    defaultPublish(content=message)


def publishDeleteFileSuccess(fileUid: str) -> None:
    content = json.dumps({
        "fileUid": fileUid,
    })
    message = json.dumps({
        "type": DELETE_FILE_SUCCESS,
        "content": content,
    }, ensure_ascii=False)
    defaultPublish(content=message)


def publishDeleteFileError(fileUid: str, errorMsg: str) -> None:
    content = json.dumps({
        "fileUid": fileUid,
        "errorMsg": errorMsg,
    })
    message = json.dumps({
        "type": DELETE_FILE_ERROR,
        "content": content,
    }, ensure_ascii=False)
    defaultPublish(content=message)


def publishAnswerMessage(
    id: int,
    uid: str,
    threadTopic: str, 
    kbUid: str, 
    question: str, 
    answer: str, 
    model: str, 
    created: int) -> None:
    # 
    content = json.dumps({
        "id": id,
        "uid": uid,
        "threadTopic": threadTopic,
        "kbUid": kbUid,
        "question": question,
        "answer": answer,
        "model": model,
        "created": created
    })
    message = json.dumps({
        "type": ANSWER,
        "content": content,
    }, ensure_ascii=False)
    defaultPublish(content=message)
    return


def publishAnswerFinished(
    id: int,
    uid: str,
    threadTopic: str, 
    kbUid: str, 
    question: str, 
    answer: str, 
    model: str, 
    created: int, 
    promptTokens: str, 
    completionTokens: str, 
    totalTokens: str) -> None:
    # 
    content = json.dumps({
        "id": id,
        "uid": uid,
        "threadTopic": threadTopic,
        "kbUid": kbUid,
        "question": question,
        "answer": answer,
        "model": model,
        "created": created,
        "promptTokens": promptTokens,
        "completionTokens": completionTokens,
        "totalTokens": totalTokens
    })
    message = json.dumps({
        "type": ANSWER_FINISHED,
        "content": content,
    }, ensure_ascii=False)
    defaultPublish(content=message)
    return

#


def defaultPublish(content: str) -> None:
    publish(pubsubChannel, content)


def publish(channel, message):
    # logging.info(f'publish {message} to channel: {channel}')
    redisClient.publish(channel, message)
