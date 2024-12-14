'''
Author: jackning 270580156@qq.com
Date: 2024-08-29 09:55:35
LastEditors: jackning 270580156@qq.com
LastEditTime: 2024-08-29 15:10:19
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
import os
import requests

def download_file(file_url: str, destination_folder: str = 'files') -> str | None:
    """
    下载文件到指定文件夹。
    
    :param file_url: 要下载的文件的URL。
    :param destination_folder: 保存文件的文件夹名称，默认为'file'。
    """
    # 确保目标文件夹存在，如果不存在则创建
    if not os.path.exists(destination_folder):
        os.makedirs(destination_folder)

    # 从URL中获取文件名
    file_name = file_url.split('/')[-1]

    # 构建完整的文件保存路径
    file_path = os.path.join(destination_folder, file_name)

    # 使用requests库下载文件
    with requests.get(file_url, stream=True) as response:
        if response.status_code == 200:
            # 以二进制写入模式打开文件
            with open(file_path, 'wb') as file:
                # 分块写入文件内容
                for chunk in response.iter_content(chunk_size=8192):
                    file.write(chunk)
            print(f"文件已成功下载到: {file_path}")
        else:
            print(f"下载失败，状态码: {response.status_code}")

    # 返回文件保存路径
    return file_path
