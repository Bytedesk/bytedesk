'''
Author: jackning 270580156@qq.com
Date: 2024-08-29 18:23:40
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-08-30 16:54:39
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
pubsubChannel: str = 'bytedeskim:pubsub'
# FIXME: 需要统一java服务器编解码 UnicodeDecodeError: 'utf-8' codec can't decode byte 0xac in position 0: invalid start byte
# pubsubObjectChannel: str = 'bytedeskim:pubsub_object'
# 
PARSE_FILE: str = 'PARSE_FILE'
PARSE_FILE_SUCCESS: str = 'PARSE_FILE_SUCCESS'
PARSE_FILE_ERROR: str = 'PARSE_FILE_ERROR'
# 
DELETE_FILE: str = 'DELETE_FILE'
DELETE_FILE_SUCCESS: str = 'DELETE_FILE_SUCCESS'
DELETE_FILE_ERROR: str = 'DELETE_FILE_ERROR'
# 
QUESTION: str = 'QUESTION'
ANSWER: str = 'ANSWER'
ANSWER_FINISHED: str = 'ANSWER_FINISHED'

